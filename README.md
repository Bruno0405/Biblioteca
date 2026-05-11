# Biblioteca

[![Quarkus](https://img.shields.io/badge/Backend-Quarkus_3.32-4695EB?logo=quarkus)](Backend/)
[![React](https://img.shields.io/badge/Frontend-React_19-61DAFB?logo=react)](Frontend/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-4169E1?logo=postgresql)](Database/)
[![License](https://img.shields.io/badge/License-Academic-blue)](#)

Full-stack library management system built as a university project. The system handles the complete lifecycle of a physical library — book cataloging, client and employee management, stock control, reservations with state-machine transitions, automatic fine generation, and audit logging.

---

## Architecture

```
┌──────────────────────────────────────────────────────────┐
│                   Frontend (React SPA)                   │
│  Client Dashboard  │  Admin Dashboard  │  Auth & Routing │
└─────────────────────┬────────────────────────────────────┘
                      │  HTTP (JSON)
                      ▼
┌──────────────────────────────────────────────────────────┐
│                   Backend (Quarkus API)                   │
│  14 domain modules  │  REST endpoints  │  Business logic │
└─────────────────────┬────────────────────────────────────┘
                      │  JDBC
                      ▼
┌──────────────────────────────────────────────────────────┐
│                 Database (PostgreSQL)                     │
│     Schema managed via Flyway migrations (V1–V8)         │
└──────────────────────────────────────────────────────────┘
```

The monorepo is organized into three directories:

| Directory | Description |
| --------- | ----------- |
| [`Backend/`](Backend/) | Java 21 + Quarkus REST API — business logic, data access, and HTTP endpoints |
| [`Frontend/`](Frontend/) | React 19 + Vite SPA — client and admin interfaces |
| [`Database/`](Database/) | Raw SQL schema files (reference copies of the database structure) |

---

## Domain Model

The backend is organized around **14 domain modules** that map directly to database tables:

```
Books ──┬── Authors (M:N via livro_autor)
        ├── Genres  (M:N via livro_genero)
        ├── Photos  (1:N)
        ├── Stock   (1:1)
        ├── Stock Movements (1:N)
        └── Reservations (1:N) ──┬── Fines
                                 └── Clients
```

Supporting modules handle **Clients**, **Employees**, **Client History** (per-client audit trail), and **Logs** (system-wide audit).

### Reservation State Machine

```
reservado ──→ emprestado ──→ devolvido
    │              │
    ├── cancelado  └── atrasado ──→ devolvido
    └── devolvido              └── cancelado
```

Each transition atomically adjusts stock counters. The `atrasado` state auto-generates a fine at R$ 2.00/day.

---

## Tech Stack

### Backend

| | |
|---|---|
| **Runtime** | Quarkus 3.32 |
| **Language** | Java 21 |
| **Framework** | REST (JAX-RS) + Hibernate ORM with Panache |
| **Database** | PostgreSQL |
| **Migrations** | Flyway |
| **API docs** | SmallRye OpenAPI / Swagger UI |
| **Validation** | Hibernate Validator |
| **Build** | Maven |

### Frontend

| | |
|---|---|
| **Framework** | React 19 |
| **Build tool** | Vite 7 |
| **Styling** | Tailwind CSS 4 |
| **Routing** | React Router 7 |
| **HTTP client** | Axios |
| **Icons** | FontAwesome 7 |
| **Linting** | ESLint 9 |

---

## Project Structure

```
Biblioteca/
  Backend/                          # Java / Quarkus REST API
    pom.xml
    src/main/java/biblioteca/
      autores/                      # Author CRUD
      clientes/                     # Client CRUD + login
      estoque/                      # Stock CRUD + availability
      fotos/                        # Photo CRUD
      funcionarios/                 # Employee CRUD + login
      generos/                      # Genre CRUD
      historico_cliente/            # Client history audit
      livro_autor/                  # Book-Author link
      livro_genero/                 # Book-Genre link
      livros/                       # Book CRUD + search
      logs/                         # System logs
      movimentacao_estoque/         # Stock movements
      multas/                       # Fine CRUD
      reservas/                     # Reservation CRUD + state machine
    src/main/resources/
      db/migration/                 # Flyway V1–V8
  Frontend/                         # React / Vite SPA
    package.json
    vite.config.js
    src/
      components/                   # Reusable UI components
      pages/                        # Login, Dashboard, AdminDashboard
      routes/                       # Auth guards
      context/                      # Auth state management
      services/                     # API communication layer
      config/                       # Resource definitions for CRUD UI
  Database/                         # Reference SQL schemas
    biblioteca03.sql
    Altera03.sql
```

---

## Documentation

- **Backend README** — [Backend/README.md](Backend/README.md)
- **Frontend README** — [Frontend/README.md](Frontend/README.md)

---

## License

Projeto acadêmico — Biblioteca.
