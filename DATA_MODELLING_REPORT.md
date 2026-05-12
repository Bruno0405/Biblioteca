# Data Modelling Report

## Overview

After a thorough exploration of the entire `Backend/` source tree (14 domain modules, 56 Java files, 8 Flyway migrations), the following structural and modelling issues were identified. The report is organized from most critical to least critical.

---

## 1. Zero JPA Relationship Mappings — All Foreign Keys Are Raw Integers

**Severity: Critical**

Every inter-entity reference in the codebase is stored as a plain `Integer` field rather than a proper JPA `@ManyToOne`, `@OneToOne`, or `@ManyToMany` relationship. This is the root cause of most other problems.

| Entity | Field | Current Type | Should Be |
|--------|-------|-------------|-----------|
| `Estoque` | `idLivro` | `Integer` | `@OneToOne Livro livro` |
| `Foto` | `idLivro` | `Integer` | `@ManyToOne Livro livro` |
| `Reserva` | `idCliente` | `Integer` | `@ManyToOne Cliente cliente` |
| `Reserva` | `idLivro` | `Integer` | `@ManyToOne Livro livro` |
| `Reserva` | `idFuncionarioRetirada` | `Integer` | `@ManyToOne Funcionario funcionarioRetirada` |
| `Reserva` | `idFuncionarioDevolucao` | `Integer` | `@ManyToOne Funcionario funcionarioDevolucao` |
| `MovimentacaoEstoque` | `idLivro` | `Integer` | `@ManyToOne Livro livro` |
| `Multa` | `idReserva` | `Integer` | `@ManyToOne Reserva reserva` |
| `HistoricoCliente` | `idCliente` | `Integer` | `@ManyToOne Cliente cliente` |
| `Log` | `idCliente` | `Integer` | `@ManyToOne Cliente cliente` |
| `Log` | `idFuncionario` | `Integer` | `@ManyToOne Funcionario funcionario` |

**Consequences:**

- No navigability from code — you cannot do `reserva.getCliente().getNomeCliente()`, you must manually query the `Cliente` repository.
- No cascading — creating a `Livro` cannot atomically persist its `Foto` or `Estoque` entries.
- No orphan removal — deleting a `Livro` does not automatically clean up its `Estoque`, `Fotos`, `MovimentacaoEstoque`, etc.
- Business logic leaks into controllers (see issue #3).

---

## 2. Join Tables Modelled as Standalone Modules Instead of JPA Relationships

**Severity: Critical**

The `livro_autor/` and `livro_genero/` modules are each a complete CRUD stack:

```
livro_autor/
  controller/LivroAutorController.java
  data/LivroAutor.java + LivroAutorId.java
  models/LivroAutorDTO.java
  repository/RepositorioLivroAutor.java

livro_genero/
  controller/LivroGeneroController.java
  data/LivroGenero.java + LivroGeneroId.java
  models/LivroGeneroDTO.java
  repository/RepositorioLivroGenero.java
```

This is unnecessary. Both are classic M:N join tables that JPA handles natively with `@ManyToMany`:

```java
// In Livro entity:
@ManyToMany
@JoinTable(
    name = "Livro_Autor",
    joinColumns = @JoinColumn(name = "id_livro"),
    inverseJoinColumns = @JoinColumn(name = "id_autor")
)
private Set<Autor> autores = new HashSet<>();

@ManyToMany
@JoinTable(
    name = "Genero_livro",
    joinColumns = @JoinColumn(name = "id_livro"),
    inverseJoinColumns = @JoinColumn(name = "id_genero")
)
private Set<Genero> generos = new HashSet<>();
```

**What to eliminate:**

- Entire `biblioteca.livro_autor` package
- Entire `biblioteca.livro_genero` package
- All four repository classes (`RepositorioLivroAutor`, `RepositorioLivroGenero`)
- All four DTO classes (`LivroAutorDTO`, `LivroGeneroDTO`)
- The `LivroAutorId` and `LivroGeneroId` composite key classes

**What to add in `Livro` entity:**

- `@ManyToMany Set<Autor> autores`
- `@ManyToMany Set<Genero> generos`

---

## 3. REST Endpoint Design Violates Conventions

**Severity: High**

Current endpoints:

```
GET    /livro-autor           — list all book-author links
GET    /livro-autor/{idL}/{idA} — find a specific link
POST   /livro-autor           — create a link
DELETE /livro-autor/{idL}/{idA} — delete a link
```

This is flat RPC-style. REST conventions for sub-resources dictate:

```
GET    /livros/{id}/autores    — list authors of a book
POST   /livros/{id}/autores    — associate authors with a book
DELETE /livros/{id}/autores/{idAutor} — remove an author from a book

GET    /autores/{id}/livros    — list books by an author
```

**Recommended approach:** Remove the `/livro-autor` and `/livro-genero` endpoints entirely. Instead:

1. **On book creation:** `POST /livros` accepts `Set<Integer> idAutores` and `Set<Integer> idGeneros` in the request body.
2. **On book read:** `GET /livros/{id}` returns the book with nested `autores: [...]` and `generos: [...]` arrays.
3. **For association management:** `POST /livros/{id}/autores` and `DELETE /livros/{id}/autores/{idAutor}` are thin wrappers over the `@ManyToMany` relationship.

---

## 4. Controller Does Business Logic — No Service Layer

**Severity: High**

`ReservaController` (the most complex controller) directly:

- Injects `RepositorioEstoque` and `RepositorioMultas`
- Manually adjusts stock counters (`quantidadeReservada++`, `quantidadeEmprestada--`, etc.)
- Calculates fine amounts (`R$ 2.00/day`)
- Creates `Multa` entities

All of this should live in a `ReservaService` (or equivalent) to keep controllers thin and testable. The same applies to `ClienteController.login()` which handles brute-force protection logic.

---

## 5. Stock Management Uses Flat IDs Instead of Object Relationships

**Severity: High**

`Estoque.idLivro` has a `UNIQUE` constraint in the database, meaning the relationship is 1:1 (one stock record per book). But instead of modelling it as `@OneToOne Livro livro`, it uses a raw `Integer`. Same applies to `MovimentacaoEstoque.idLivro`.

The `ReservaController`'s stock adjustments would become trivial if the relationship was navigable:

```java
// Instead of:
Estoque estoque = repositorioEstoque.find("idLivro", reserva.getIdLivro()).firstResult();
estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - 1);

// Could be:
reserva.getLivro().getEstoque().setQuantidadeReservada(...);
```

---

## 6. Inconsistent Join Table Naming

**Severity: Low**

| Table | Name Pattern | Notes |
|-------|-------------|-------|
| `Livro_Autor` | `EntityA_EntityB` — book-first | Consistent with `Livro` being the main entity |
| `Genero_livro` | `EntityB_EntityA` — genre-first | Inverted — should be `Livro_Genero` |

Both should follow the same convention. Since `Livro` is the central aggregate, `Livro_Genero` would be consistent with `Livro_Autor`.

---

## 7. DTOs Duplicate Entity Fields Exactly

**Severity: Low**

Every DTO is a 1:1 mirror of its entity with identical fields and getters/setters. This creates a lot of boilerplate with no real benefit. The only case where DTOs differ is `ReservaDTO.setStatusReserva()` which defaults to `"reservado"`.

Consider using `MapStruct`, Java Records (Java 21), or simply using the entity directly for simple CRUD scenarios.

---

## 8. (Bonus) Plain Text Passwords

**Severity: Security**

Both `Cliente.senhaCliente` and `Funcionario.senha` are stored and compared as plaintext. While not a data-modelling issue per se, any schema migration should add a `password_hash` column and remove the plaintext column.

---

## Summary of Recommendations

| Priority | Change | Effort |
|----------|--------|--------|
| 1 | Add `@ManyToOne`, `@OneToOne`, `@ManyToMany` to all entities | High |
| 2 | Remove `livro_autor` and `livro_genero` modules | Medium |
| 3 | Refactor `LivroDTO` to accept/return nested `autores` and `generos` | Medium |
| 4 | Move business logic from controllers into a service layer | Medium |
| 5 | Use proper REST sub-resource paths (`/livros/{id}/autores`) | Medium |
| 6 | Rename `Genero_livro` to `Livro_Genero` for consistency | Low |
| 7 | Eliminate redundant DTOs or migrate to Java Records | Low |
| 8 | Hash passwords (schema + code change) | Low |

---

*Generated from codebase analysis — all 14 modules reviewed.*
