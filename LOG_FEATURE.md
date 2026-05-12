# Log Feature — System-wide Audit Logging

## Summary

Create a `LogService` that encapsulates the persistence of `Log` records, then wire it into every write operation across all 12 backend modules. This gives us a centralized audit trail of every mutation in the system.

---

## Scope

| In scope | Out of scope |
|---|---|
| `LogService` class in `biblioteca.logs.services` | Authentication / identity (`idCliente`, `idFuncionario`, `ip` fields — deferred) |
| Wiring `LogService.log()` into every POST, PUT, DELETE across all controllers | READ operations (GET) — no audit needed for reads |
| Action string convention for log entries | UI changes |
| Handling the `reservas` internal side-effect (auto fine generation) | Migration changes — the `Logs` table already exists |
| Logging of `POST /clientes/login` and `POST /funcionarios/login` (these mutate state: tentativas, bloqueio) | |

---

## New File — `LogService`

**Location:** `Backend/src/main/java/biblioteca/logs/services/LogService.java`

**Package:** `biblioteca.logs.services`

### Design

```java
package biblioteca.logs.services;

import biblioteca.logs.data.Log;
import biblioteca.logs.repository.RepositorioLogs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class LogService {

    @Inject
    RepositorioLogs repositorioLogs;

    @Transactional
    public void log(Log log) {
        if (log.getDataAcao() == null) {
            log.setDataAcao(LocalDateTime.now());
        }
        // Identity fields deliberately omitted:
        // idCliente, idFuncionario, ip — to be added later.
        // They remain null for now.
        repositorioLogs.persist(log);
    }
}
```

### Rationale

- **`@ApplicationScoped`** — matches the existing pattern used by all repositories.
- **`@Transactional`** — required because this is a write operation; the caller must already be inside a transaction when calling `persist()`. Since all controller write methods already carry `@Transactional`, the service's `@Transactional` will join the same transaction (REQUIRED is the default).
- **`dataAcao` default** — set to `LocalDateTime.now()` if not provided, so the caller only needs to supply `acao`.
- **Identity fields null** — `idCliente`, `idFuncionario`, `ip` are left null per the explicit directive ("Ensure the 'identity' concern stays off for now"). The database schema already allows NULL for these columns.

---

## Integration Pattern

### Action string convention

Every log entry needs a human-readable `acao` describing what happened. Use this convention:

| Operation | Pattern | Example |
|---|---|---|
| POST (create) | `"Criou {entidade}"` | `"Criou autor"` |
| PUT (update) | `"Atualizou {entidade} (id: {id})"` | `"Atualizou cliente (id: 42)"` |
| DELETE (remove) | `"Removeu {entidade} (id: {id})"` | `"Removeu livro (id: 17)"` |
| POST /login | `"Login de {tipo} (id: {id})"` | `"Login de cliente (id: 12)"` |

Where `{entidade}` is the singular, lowercase entity name in Portuguese (e.g., `autor`, `livro`, `reserva`, `estoque`, `multa`, `foto`, `genero`, `cliente`, `funcionario`, `movimentacao`, `historico`, `log`).

### Injection pattern

In every controller, add:

```java
@Inject
LogService logService;
```

Then in each write method, after the successful persistence (but before the return), call:

```java
Log logEntry = new Log();
logEntry.setAcao("Criou autor");
logService.log(logEntry);
```

For updates and deletes where the entity ID is known:

```java
Log logEntry = new Log();
logEntry.setAcao("Atualizou autor (id: " + id + ")");
logService.log(logEntry);
```

### Important: Transaction boundary

The `logService.log()` call must happen **inside** the `@Transactional` method but **after** the primary entity operation (`persist`/`deleteById`) succeeds. This ensures both the primary mutation and the log entry commit atomically. If the primary operation fails, the log entry is also rolled back — which is the desired behavior.

---

## Controller-by-controller modifications

### 1. `autores/controller/AutorController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou autor"` | After `repositorioAutores.persist(autor)` (line 45) |
| `PUT` atualizar | `"Atualizou autor (id: " + id + ")"` | After `repositorioAutores.persist(autor)` (line 60) |
| `DELETE` deletar | `"Removeu autor (id: " + id + ")"` | After deletion check passes (line 84) |

### 2. `clientes/controller/ClienteController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou cliente"` | After `repositorioClientes.persist(cliente)` (line 45) |
| `POST /login` (bloqueio) | `"Cliente bloqueado (id: " + cliente.getIdCliente() + ")"` | After the `bloqueado=true` persist (line 84), before the error return |
| `POST /login` (success) | `"Login de cliente (id: " + cliente.getIdCliente() + ")"` | After `repositorioClientes.persist(cliente)` (line 100) |
| `PUT` atualizar | `"Atualizou cliente (id: " + id + ")"` | After `repositorioClientes.persist(cliente)` (line 122) |
| `DELETE` deletar | `"Removeu cliente (id: " + id + ")"` | After deletion check passes (line 133) |

Note: The `POST /login` failed-attempt branch (line 91, `tentativas` incremented to < 5) also persists. Should it be logged? Recommendation: yes — `"Tentativa de login falhou (cliente id: " + cliente.getIdCliente() + ")"`. This is important for audit.

### 3. `estoque/controller/EstoqueController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou estoque"` | After `repositorioEstoque.persist(estoque)` (line 57) |
| `PUT` atualizar | `"Atualizou estoque (id: " + id + ")"` | After `repositorioEstoque.persist(estoque)` (line 78) |
| `DELETE` deletar | `"Removeu estoque (id: " + id + ")"` | After deletion check passes (line 89) |

### 4. `fotos/controller/FotoController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou foto"` | After `repositorioFotos.persist(foto)` (line 44) |
| `PUT` atualizar | `"Atualizou foto (id: " + id + ")"` | After `repositorioFotos.persist(foto)` (line 61) |
| `DELETE` deletar | `"Removeu foto (id: " + id + ")"` | After deletion check passes (line 72) |

### 5. `funcionarios/controller/FuncionarioController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou funcionario"` | After `repositorioFuncionarios.persist(funcionario)` (line 45) |
| `POST /login` (success) | `"Login de funcionario (id: " + funcionario.getIdFuncionario() + ")"` | After `repositorioFuncionarios.persist` (not used — login is read-only here). Actually: the funcionario login does NOT persist anything. So no login logging needed. |
| `PUT` atualizar | `"Atualizou funcionario (id: " + id + ")"` | After `repositorioFuncionarios.persist(funcionario)` (line 89) |
| `DELETE` deletar | `"Removeu funcionario (id: " + id + ")"` | After deletion check passes (line 100) |

Note: `POST /funcionarios/login` does not mutate state (no `persist` call), so it is **not** a write operation for logging purposes.

### 6. `generos/controller/GeneroController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou genero"` | After `repositorioGeneros.persist(genero)` (line 45) |
| `PUT` atualizar | `"Atualizou genero (id: " + id + ")"` | After `repositorioGeneros.persist(genero)` (line 61) |
| `DELETE` deletar | `"Removeu genero (id: " + id + ")"` | After deletion check passes (line 85) |

### 7. `historico/controller/HistoricoClienteController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou historico"` | After `repositorioHistoricoCliente.persist(historico)` (line 44) |
| `DELETE` deletar | `"Removeu historico (id: " + id + ")"` | After deletion check passes (line 58) |

### 8. `livros/controller/LivroController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou livro"` | After `repositorioLivros.persist(livro)` (line 101) |
| `POST /{id}/autores` | `"Adicionou autor(es) ao livro (id: " + id + ")"` | After `repositorioLivros.persist(livro)` (line 168) |
| `POST /{id}/generos` | `"Adicionou genero(s) ao livro (id: " + id + ")"` | After `repositorioLivros.persist(livro)` (line 208) |
| `PUT` atualizar | `"Atualizou livro (id: " + id + ")"` | After `repositorioLivros.persist(livro)` (line 138) |
| `DELETE /{id}` | `"Removeu livro (id: " + id + ")"` | After deletion check passes (line 238) |
| `DELETE /{id}/autores/{idAutor}` | `"Removeu autor do livro (livro id: " + id + ", autor id: " + idAutor + ")"` | After `repositorioLivros.persist(livro)` (line 181) |
| `DELETE /{id}/generos/{idGenero}` | `"Removeu genero do livro (livro id: " + id + ", genero id: " + idGenero + ")"` | After `repositorioLivros.persist(livro)` (line 221) |

### 9. `logs/controller/LogController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou log"` | After `repositorioLogs.persist(log)` (line 45) — this uses the **repository**, not the service (to avoid recursion). |
| `DELETE` deletar | `"Removeu log (id: " + id + ")"` | After deletion check passes (line 59) |

**Design decision:** The `LogController`'s own POST creates a `Log` entry too — but it uses `RepositorioLogs` directly rather than `LogService` to avoid infinite recursion (`LogService.log()` calling `RepositorioLogs.persist()`, then `LogService.log()` again...). The `DELETE` in `LogController` also logs via the repository directly, using a manually constructed `Log` object.

### 10. `movimentacao/controller/MovimentacaoEstoqueController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou movimentacao"` | After `repositorioMovimentacaoEstoque.persist(movimentacao)` (line 44) |
| `DELETE` deletar | `"Removeu movimentacao (id: " + id + ")"` | After deletion check passes (line 58) |

### 11. `multas/controller/MultaController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou multa"` | After `repositorioMultas.persist(multa)` (line 57) |
| `PUT` atualizar | `"Atualizou multa (id: " + id + ")"` | After `repositorioMultas.persist(multa)` (line 77) |
| `DELETE` deletar | `"Removeu multa (id: " + id + ")"` | After deletion check passes (line 88) |

### 12. `reservas/controller/ReservaController.java`

| Method | Action string | Insert location |
|---|---|---|
| `POST` criar | `"Criou reserva"` + include book availability info? Simple: `"Criou reserva"` | After `repositorioReservas.persist(reserva)` (line 97) |
| `PUT` atualizar | `"Atualizou reserva (id: " + id + ") para " + novoStatus` | After all state-machine logic, after `repositorioReservas.persist(reserva)` (line 194) |
| `DELETE` deletar | `"Removeu reserva (id: " + id + ")"` | After deletion check passes (line 205) |

For the `PUT`, the action string should reflect the state transition since that's the key business event:
```java
logEntry.setAcao("Atualizou reserva (id: " + id + ") para " + novoStatus);
```

When a fine is auto-generated inside `PUT` (the `atrasado` transition), the action string for the reservation update already captures this event. No separate log entry is needed for the fine creation since it's a side effect of the reservation transition.

---

## Implementation Order

| Step | What | Files touched |
|---|---|---|
| 1 | Create `LogService.java` | 1 new file |
| 2 | Wire into `AutorController` | 1 modified file (+ 6 lines) |
| 3 | Wire into `ClienteController` | 1 modified file (+ ~14 lines, login branches) |
| 4 | Wire into `EstoqueController` | 1 modified file (+ 6 lines) |
| 5 | Wire into `FotoController` | 1 modified file (+ 6 lines) |
| 6 | Wire into `FuncionarioController` | 1 modified file (+ 6 lines) |
| 7 | Wire into `GeneroController` | 1 modified file (+ 6 lines) |
| 8 | Wire into `HistoricoClienteController` | 1 modified file (+ 4 lines) |
| 9 | Wire into `LivroController` | 1 modified file (+ ~18 lines, 7 operations) |
| 10 | Wire into `LogController` | 1 modified file (manual Log persistence, + 4 lines) |
| 11 | Wire into `MovimentacaoEstoqueController` | 1 modified file (+ 4 lines) |
| 12 | Wire into `MultaController` | 1 modified file (+ 6 lines) |
| 13 | Wire into `ReservaController` | 1 modified file (+ 6 lines) |

**Total:** 1 new file, 12 modified files.

---

## Architecture Decision Records

### ADR-1: Why a service class instead of a CDI interceptor

**Context:** All 12 controllers need to persist a Log entry on write operations. An alternative approach is a CDI interceptor annotated with `@Loggable` that captures method entry.

**Decision:** Use an explicit `LogService` injected into each controller.

**Consequences:**
- **Positive:** Transparent, explicit, debuggable. A developer reading a controller sees exactly when and what gets logged.
- **Positive:** Zero magic — matches the existing codebase style where all logic lives in controllers.
- **Negative:** Boilerplate — each write method gets 3 extra lines (create Log, set acao, call service). This is acceptable for a codebase this size (32 write operations).

### ADR-2: The `LogController` does not inject `LogService`

**Context:** `LogController`'s own write operations would trigger a `LogService.log()` call, which leads to a self-referential call chain (LogService.log → RepositorioLogs.persist → ... → LogController if logging were in the service too).

**Decision:** `LogController` uses `RepositorioLogs` directly (as it currently does) instead of `LogService`. For its own writes, it manually constructs a `Log` object and persists it.

**Consequences:**
- **Positive:** No recursion risk.
- **Negative:** Slightly inconsistent — one controller doesn't use LogService. This is documented and intentional.

### ADR-3: Action string as a hardcoded string, not an enum

**Context:** Each write method needs a descriptive action string for the `acao` field.

**Decision:** Use inline string literals following the convention table above.

**Consequences:**
- **Positive:** Simple, readable, no new types. Each log entry is self-documenting.
- **Positive:** Easy to grep for log strings when debugging.
- **Negative:** Strings can drift if method names change. Acceptable for an academic project with low change frequency.

---

## Verification

After implementation, verify by:

1. **Compilation check:** `./mvnw compile` in `Backend/` must pass.
2. **Smoke test:** Start the server, make a POST/PUT/DELETE call to any endpoint, then `GET /logs` to confirm the entry appears with the correct `acao` and `dataAcao`.
3. **Edge case:** Confirm `LogController`'s own POST works (creates a log entry about creating a log entry).
4. **Edge case:** Confirm identity fields (`idCliente`, `idFuncionario`, `ip`) remain null in the database after logging.

---

## Open Questions

1. **Should `POST /clientes/login` failed tentativas (< 5) be logged?** This is a write (tentativas incremented) and is relevant for security audit. Recommended: log as `"Tentativa de login falhou (cliente id: ...)"` — but this is up to the implementer.

2. **Should the LogController's own write operations be logged at all?** The plan above says yes (for consistency). An alternative is to skip logging the Log module's own writes entirely, since they are meta and potentially noisy. Decision deferred to implementer; the plan includes them by default.

3. **Should `FuncionarioController.POST /login` (which is read-only) be excluded from logging?** Yes — excluded. No state mutation occurs.
