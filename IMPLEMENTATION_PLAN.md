# Implementation Plan — Data Model Refactoring

## Scope

Refactor the Backend's data model to use proper JPA relationships, eliminate the standalone `livro_autor` and `livro_genero` modules, and redesign the REST endpoints to follow sub-resource conventions.

---

## Architecture Decisions

### ADR-1: Bidirectional vs. Unidirectional Relationships

**Context:** Adding JPA relationships means choosing between unidirectional (only the FK-holding side has `@ManyToOne`) and bidirectional (the inverse side also has `@OneToMany`). Bidirectional adds complexity (`mappedBy`, `@JsonManagedReference`/`@JsonBackReference`).

**Decision:** Use bidirectional relationships **only** for Livro (the aggregate root). All other entities get unidirectional `@ManyToOne` only. This keeps changes minimal while making the main navigability path work.

**Consequence:** `reserva.getCliente().getNomeCliente()` works. `cliente.getReservas()` does not exist unless explicitly needed later.

### ADR-2: JSON Circular Reference Strategy

**Context:** `@ManyToOne` and `@OneToMany` create circular references during serialization (Livro → Estoque → Livro → ...).

**Decision:** Use `@JsonIgnoreProperties` on the relationship field to break the cycle. Specifically, annotate every entity-level relationship field with `@JsonIgnoreProperties("livro")` (or the relevant inverse field). This avoids heavy annotations like `@JsonManagedReference`/`@JsonBackReference` and allows selective exposure.

**Consequence:** Serialization is safe. The inverse side is excluded from JSON by default. DTOs remain the serialization boundary for complex cases.

### ADR-3: Keep Old Integer Fields as Readonly During Migration

**Context:** Adding `@ManyToOne Livro livro` with `@JoinColumn(name = "id_livro")` conflicts with the existing `Integer idLivro` field that writes to the same column.

**Decision:** Keep the old Integer field but annotate it with `@Column(insertable = false, updatable = false)`. This makes it read-only for JPA while existing code that reads `getIdLivro()` still works. After all code is migrated to use object references, the Integer fields can be removed.

**Consequence:** Two-phase migration. Phase 1 is purely additive (no existing code breaks). Phase 2 removes the old Integer fields after all references are updated.

### ADR-4: No Service Layer Yet

**Context:** The report identified business logic in controllers (ReservaController). Adding a service layer is architecturally correct but is a separate concern from the data model refactoring.

**Decision:** Defer service layer extraction to a follow-up. The immediate refactoring keeps controller logic in place but updates it to use JPA object navigation instead of repository queries.

**Consequence:** Controllers remain "fat" but become more readable (e.g., `reserva.getLivro().getEstoque()` instead of `repositorioEstoque.find("idLivro", ...)`).

---

## Phases

Each phase is a self-contained unit of work that leaves the codebase in a compilable, testable state.

---

### Phase 1 — Add JPA Relationships to All Entities (Backend Only)

**Goal:** Every FK field (`idLivro`, `idCliente`, `idReserva`, etc.) gains a companion `@ManyToOne` or `@OneToOne` object reference. Old Integer fields are kept as read-only. This phase is purely additive — no existing code breaks.

**Files to modify:**

| File | Change |
|------|--------|
| `Estoque.java` | Add `@OneToOne @JoinColumn(name = "id_livro", insertable = false, updatable = false) private Livro livro;` |
| `Foto.java` | Add `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_livro", insertable = false, updatable = false) private Livro livro;` |
| `MovimentacaoEstoque.java` | Add `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_livro", insertable = false, updatable = false) private Livro livro;` |
| `Reserva.java` | Add 4 `@ManyToOne` fields: `cliente`, `livro`, `funcionarioRetirada`, `funcionarioDevolucao` (all with `insertable = false, updatable = false`) |
| `Multa.java` | Add `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_reserva", insertable = false, updatable = false) private Reserva reserva;` |
| `HistoricoCliente.java` | Add `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_cliente", insertable = false, updatable = false) private Cliente cliente;` |
| `Log.java` | Add 2 `@ManyToOne` fields: `cliente`, `funcionario` (both with `insertable = false, updatable = false`) |

**Detail — `Reserva.java` additions:**

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_cliente", insertable = false, updatable = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Cliente cliente;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_livro", insertable = false, updatable = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Livro livro;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_funcionario_retirada", insertable = false, updatable = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Funcionario funcionarioRetirada;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_funcionario_devolucao", insertable = false, updatable = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Funcionario funcionarioDevolucao;
```

**Detail — `Estoque.java` addition:**

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_livro", insertable = false, updatable = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Livro livro;
```

**Same pattern for all others.** Every new relationship field:
- Uses `FetchType.LAZY` to avoid N+1 queries
- References the existing FK column via `@JoinColumn`
- Marks itself read-only (`insertable = false, updatable = false`) so the old Integer field still writes
- Adds `@JsonIgnoreProperties` to prevent Hibernate proxy serialization errors

**Verification:** The app compiles and starts. All existing endpoints return identical JSON (the new fields are ignored by Jackson unless exposed in DTOs).

---

### Phase 2 — Add Inverse `@ManyToMany` and `@OneToMany` on Livro

**Goal:** Make `Livro` the aggregate root. It gains collection relationships for authors, genres, stock, photos, movements, and reservations.

**Detail — additions to `Livro.java`:**

```java
// M:N relationships
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "Livro_Autor",
    joinColumns = @JoinColumn(name = "id_livro"),
    inverseJoinColumns = @JoinColumn(name = "id_autor")
)
@JsonIgnoreProperties("livros")
private Set<Autor> autores = new HashSet<>();

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "Genero_livro",
    joinColumns = @JoinColumn(name = "id_livro"),
    inverseJoinColumns = @JoinColumn(name = "id_genero")
)
@JsonIgnoreProperties("livros")
private Set<Genero> generos = new HashSet<>();

// 1:N relationships
@OneToOne(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties("livro")
private Estoque estoque;

@OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonIgnoreProperties("livro")
private Set<Foto> fotos = new HashSet<>();

@OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
@JsonIgnoreProperties("livro")
private Set<MovimentacaoEstoque> movimentacoes = new HashSet<>();

@OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
@JsonIgnoreProperties("livro")
private Set<Reserva> reservas = new HashSet<>();
```

**Add inverse side to `Autor.java` and `Genero.java`** (needed for `@ManyToMany(mappedBy = ...)`):

```java
// In Autor.java:
@ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
@JsonIgnoreProperties("autores")
private Set<Livro> livros = new HashSet<>();

// In Genero.java:
@ManyToMany(mappedBy = "generos", fetch = FetchType.LAZY)
@JsonIgnoreProperties("generos")
private Set<Livro> livros = new HashSet<>();
```

**Verification:** The app compiles and starts. Existing JSON output is unchanged (the new collections are not exposed in DTOs yet).

---

### Phase 3 — Update LivroDTO to Include Authors and Genres

**Goal:** `POST /livros` can accept author/genre IDs. `GET /livros/{id}` returns nested author/genre data.

**Changes to `LivroDTO.java`:**

```java
// New fields for input (creation/update)
private Set<Integer> idAutores;
private Set<Integer> idGeneros;

// New fields for output (response only)
private List<AutorDTO> autores;
private List<GeneroDTO> generos;
```

The existing scalar fields (`nomeLivro`, `isnb`, etc.) remain unchanged.

**Changes to `LivroController.java`:**

```java
@POST
@Transactional
public Response criar(LivroDTO livroDTO) {
    Livro livro = transformeEmEntidade(livroDTO);
    
    // Associate authors and genres
    if (livroDTO.getIdAutores() != null) {
        livro.setAutores(new HashSet<>(
            repositorioAutores.list("idAutor IN ?1", livroDTO.getIdAutores())
        ));
    }
    if (livroDTO.getIdGeneros() != null) {
        livro.setGeneros(new HashSet<>(
            repositorioGeneros.list("idGenero IN ?1", livroDTO.getIdGeneros())
        ));
    }
    
    repositorioLivros.persist(livro);
    return Response.status(Response.Status.CREATED)
        .entity(tranformeEmDto(livro))
        .build();
}
```

**Update `tranformeEmDto` to populate nested DTOs:**

```java
private LivroDTO tranformeEmDto(Livro livro) {
    LivroDTO dto = new LivroDTO();
    // ... existing scalar fields ...
    
    // Nested collections
    if (livro.getAutores() != null) {
        dto.setAutores(livro.getAutores().stream()
            .map(a -> { AutorDTO ad = new AutorDTO(); ad.setIdAutor(a.getIdAutor()); ad.setNomeAutor(a.getNomeAutor()); return ad; })
            .toList());
    }
    if (livro.getGeneros() != null) {
        dto.setGeneros(livro.getGeneros().stream()
            .map(g -> { GeneroDTO gd = new GeneroDTO(); gd.setIdGenero(g.getIdGenero()); gd.setNomeGenero(g.getNomeGenero()); return gd; })
            .toList());
    }
    return dto;
}
```

**Inject `RepositorioAutores` and `RepositorioGeneros`** into `LivroController`.

**Same patterns for `PUT /livros/{id}`:** Re-sync the author/genre sets on update.

**Verification:** Start the app. `POST /livros` with `{"nomeLivro":"...", "idAutores":[1,2], "idGeneros":[3]}` creates a book with associations. `GET /livros/1` returns nested `autores` and `generos` arrays.

---

### Phase 4 — Add Sub-Resource Endpoints to LivroController

**Goal:** REST-compliant endpoints for managing individual associations.

**Add to `LivroController.java`:**

```java
// GET /livros/{id}/autores
@GET
@Path("/{id}/autores")
public Response listarAutores(@PathParam("id") Integer id) {
    Livro livro = repositorioLivros.findById(id);
    if (livro == null) return Response.status(404).build();
    List<AutorDTO> autores = livro.getAutores().stream()
        .map(a -> { ... })
        .toList();
    return Response.ok(autores).build();
}

// POST /livros/{id}/autores — associate authors
@POST
@Path("/{id}/autores")
@Transactional
public Response adicionarAutores(@PathParam("id") Integer id, Set<Integer> idAutores) {
    Livro livro = repositorioLivros.findById(id);
    if (livro == null) return Response.status(404).build();
    Set<Autor> autores = new HashSet<>(repositorioAutores.list("idAutor IN ?1", idAutores));
    livro.getAutores().addAll(autores);
    repositorioLivros.persist(livro);
    return Response.ok(tranformeEmDto(livro)).build();
}

// DELETE /livros/{id}/autores/{idAutor}
@DELETE
@Path("/{id}/autores/{idAutor}")
@Transactional
public Response removerAutor(@PathParam("id") Integer id, @PathParam("idAutor") Integer idAutor) {
    Livro livro = repositorioLivros.findById(id);
    if (livro == null) return Response.status(404).build();
    livro.getAutores().removeIf(a -> a.getIdAutor().equals(idAutor));
    repositorioLivros.persist(livro);
    return Response.noContent().build();
}
```

**Same pattern for `/livros/{id}/generos`.**

**Add `/autores/{id}/livros`** (read-only, convenience):

```java
// In AutorController.java:
@GET
@Path("/{id}/livros")
public Response listarLivros(@PathParam("id") Integer id) {
    Autor autor = repositorioAutores.findById(id);
    if (autor == null) return Response.status(404).build();
    return Response.ok(autor.getLivros().stream().map(l -> ...).toList());
}
```

**Verification:** `POST /livros/1/autores` with `[2, 3]` associates authors. `DELETE /livros/1/autores/2` removes the association. `GET /autores/1/livros` lists books.

---

### Phase 5 — Remove livro_autor and livro_genero Modules

**Goal:** Delete the standalone modules. Their functionality is now handled by the `@ManyToMany` relationships on `Livro`.

**Delete these directories (8 files total):**

```
Backend/src/main/java/biblioteca/livro_autor/
  controller/LivroAutorController.java      — DELETE
  data/LivroAutor.java                      — DELETE
  data/LivroAutorId.java                    — DELETE
  models/LivroAutorDTO.java                 — DELETE
  repository/RepositorioLivroAutor.java     — DELETE

Backend/src/main/java/biblioteca/livro_genero/
  controller/LivroGeneroController.java     — DELETE
  data/LivroGenero.java                     — DELETE
  data/LivroGeneroId.java                   — DELETE
  models/LivroGeneroDTO.java                — DELETE
  repository/RepositorioLivroGenero.java    — DELETE
```

**Note:** No imports reference these packages from outside (checked via grep). However, verify that no other file imports from `biblioteca.livro_autor` or `biblioteca.livro_genero` before deleting.

**Verification:** The app compiles and starts. The old `/livro-autor` and `/livro-genero` endpoints return 404 (expected — they are replaced by sub-resources).

---

### Phase 6 — Update Frontend

**Goal:** Remove standalone association UIs, integrate author/genre management into the book form, update the Dashboard to use embedded data.

**6a — Remove resource configs:**

In `Frontend/src/config/resources.js`, remove these entries:
```js
{ key: "livro-autor", ... }    // lines 98-108
{ key: "livro-genero", ... }   // lines 109-119
```

**6b — Update AdminDashboard navigation:**

In `Frontend/src/pages/AdminDashboard.jsx`:
- Remove `"livro-autor"` and `"livro-genero"` from the `"Acervo"` keys array (line 47)
- Remove the icon entries (lines 37-38)

**6c — Update Dashboard client view:**

In `Frontend/src/pages/Dashboard.jsx`:
- Remove the `/livro-autor` and `/livro-genero` API calls (lines 291, 293)
- Remove the `livroAutores` and `livroGeneros` state variables (lines 261, 263)
- Replace the `autorPorLivro` and `generoPorLivro` `useMemo` computations (lines 350-371) with direct access from the books data

**New approach for Dashboard:**

```jsx
const autorPorLivro = useMemo(() => {
  const map = new Map();
  livros.forEach((livro) => {
    if (livro.autores) {
      map.set(Number(livro.idLivro), livro.autores.map(a => a.nomeAutor).join(", "));
    }
  });
  return map;
}, [livros]);

const generoPorLivro = useMemo(() => {
  const map = new Map();
  livros.forEach((livro) => {
    if (livro.generos && livro.generos.length > 0) {
      map.set(Number(livro.idLivro), livro.generos[0].nomeGenero);
    }
  });
  return map;
}, [livros]);
```

**6d — Update book form** (if a dedicated form exists, typically in a CRUD admin page):
- Add multi-select fields for authors and genres
- The form sends `idAutores` and `idGeneros` as arrays of integers alongside the book data

**Verification:** Dashboard loads without 404 errors. Book details show authors and genres. Admin CRUD can manage associations through the book form.

---

### Phase 7 (Optional) — Cleanup Old Integer Fields

**Goal:** After verifying everything works with object references, remove the old Integer FK fields.

**Migration per entity:**

```java
// BEFORE:
@Column(name = "id_livro", insertable = false, updatable = false)
private Integer idLivro;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_livro")
private Livro livro;

// AFTER:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_livro")
private Livro livro;
```

Remove the `insertable = false, updatable = false` from the relationship and delete the Integer field entirely. Then update any code that still calls `getIdLivro()` to use `getLivro().getIdLivro()` instead.

This is safest as a separate PR after everything is stable.

---

### Phase 8 (Optional) — Schema Rename Genero_livro → Livro_Genero

Create a Flyway migration `V9__Rename_genero_livro_to_livro_genero.sql`:

```sql
ALTER TABLE IF EXISTS Genero_livro RENAME TO Livro_Genero;
```

Update the `@JoinTable(name = "Genero_livro")` in `Livro.java` to `@JoinTable(name = "Livro_Genero")`.

**Low priority** — only if consistency matters to the project.

---

## Dependency Graph

```
Phase 1 (Add @ManyToOne/@OneToOne)
    │
    ▼
Phase 2 (Add @ManyToMany/@OneToMany on Livro)
    │
    ▼
Phase 3 (Update LivroDTO + LivroController)
    │
    ├────────────────────┐
    ▼                    ▼
Phase 4 (Sub-resource  Phase 5 (Delete old
 endpoints)             modules)
    │                    │
    └────────┬───────────┘
             ▼
      Phase 6 (Frontend)
             │
      Optional phases...
```

Phase 1 and 2 must be done first (they are the foundation). Phases 3, 4, and 5 can be done in parallel after Phase 2. Phase 6 (Frontend) depends on all backend changes being complete.

---

## Risk Assessment

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| Circular JSON serialization | High | `@JsonIgnoreProperties` on every relationship field. Test with a GET endpoint after Phase 2. |
| N+1 query problem | Medium | All relationships use `FetchType.LAZY`. Use `@EntityGraph` or Panache `find()` with joins if needed later. |
| Existing code still calls `getIdLivro()` | Low (initially) | The old Integer field is kept as read-only. Getters still return the correct value. |
| Transaction boundary issues with `@ManyToMany` | Low | The `@Transactional` on controller methods covers the persist. JPA cascades the join table inserts. |
| Frontend form for book creation needs redesign | Medium | The current `resourceService.js` is generic. Adding author/genre fields to the book form requires a custom implementation or extending the generic CRUD UI. |

---

## Verification Checklist

After each phase:

- [ ] `./mvnw compile` succeeds
- [ ] `./mvnw test` passes
- [ ] App starts (`./mvnw quarkus:dev`)
- [ ] Swagger UI loads at `/q/swagger-ui`
- [ ] Existing endpoints return expected JSON
- [ ] Flyway migrations apply without errors (no schema changes needed for Phases 1-6)

After Phase 3:

- [ ] `POST /livros` with `idAutores` and `idGeneros` creates associations atomically
- [ ] `GET /livros/{id}` returns `autores: [...]` and `generos: [...]`
- [ ] `PUT /livros/{id}` with updated `idAutores`/`idGeneros` syncs correctly

After Phase 4:

- [ ] `GET /livros/{id}/autores` returns author list
- [ ] `POST /livros/{id}/autores` with `[1,2,3]` associates authors
- [ ] `DELETE /livros/{id}/autores/{idAutor}` removes one association
- [ ] `GET /autores/{id}/livros` returns books for an author

After Phase 5:

- [ ] Old `/livro-autor` and `/livro-genero` return 404
- [ ] No compilation errors from deleted packages

After Phase 6 (Frontend):

- [ ] Dashboard loads without network errors
- [ ] Book cards show author and genre names correctly
- [ ] Admin dashboard removes "Livro x Autor" and "Livro x Genero" menu items
- [ ] Book create/edit form supports author and genre selection
