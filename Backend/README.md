# Biblioteca — Backend

[![Quarkus](https://img.shields.io/badge/Quarkus-3.32-4695EB?logo=quarkus)](https://quarkus.io)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql)](https://postgresql.org)
[![Flyway](https://img.shields.io/badge/Flyway-CC0200?logo=flyway)](https://flywaydb.org)

REST API for a library management system, built with **Quarkus** and **Java 21**. Provides full CRUD operations, reservation lifecycle with state-machine transitions, automatic fine generation, stock control, role-based authentication, and audit logging.

---

## Technologies

### Core

| | |
|---|---|
| **Runtime** | Quarkus 3.32 |
| **Language** | Java 21 |
| **Framework** | REST (JAX-RS) + Hibernate ORM with Panache |
| **Build tool** | Maven (with Maven Wrapper) |
| **Database** | PostgreSQL |

### Extensions

| | |
|---|---|
| **Migrations** | Flyway |
| **Validation** | Hibernate Validator (Bean Validation) |
| **API docs** | SmallRye OpenAPI / Swagger UI |
| **Health** | SmallRye Health (liveness + readiness) |
| **HTTP client** | REST Client + REST Client Jackson |
| **JSON** | REST Jackson |

---

## Architecture

The backend is organized into **12 domain modules**, each following a uniform layered structure inside `biblioteca/`:

```
biblioteca/
  autores/               Authors
  clientes/              Clients
  estoque/               Stock / Inventory
  fotos/                 Book photos
  funcionarios/          Staff / Employees
  generos/               Genres
  historico/             Client change history
  livros/                Books (embeds authors & genres via JPA)
  logs/                  System-wide audit logs
  movimentacao/          Stock movements (entry, loss, adjustment, damaged)
  multas/                Fines
  reservas/              Reservations / Loans
```

Every module follows four sub-packages:

| Package      | Responsibility |
| ------------ | -------------- |
| `controller/` | JAX-RS REST endpoints |
| `data/`       | JPA entity (extends `PanacheEntityBase`) |
| `models/`     | DTOs used for request/response |
| `repository/` | Panache repository (extends `PanacheRepository`) |

---

## Modules Overview

| Module | Description |
| ------ | ----------- |
| **Books** | Book catalog with name, ISBN, publisher, year, synopsis, physical location. Supports case-insensitive search by name and publisher. Authors and genres are managed via JPA `@ManyToMany` relationships — accepted and returned inline on book create/read/update. |
| **Authors** | Author registry. |
| **Genres** | Literary genres. |
| **Photos** | URL-based photo attachments for books. |
| **Stock** | Per-book inventory with total, reserved, loaned, and damaged counters. Filterable by book and availability. |
| **Stock Movements** | Records of entry, loss, adjustment, or damaged events tied to a book. |
| **Clients** | Client registry with login, CPF, contact info, address. Brute-force protection — account blocks after 5 failed login attempts. |
| **Employees** | Staff registry with role-based access (`A`dmin, `G`erente, `F`uncionario). |
| **Reservations** | Full reservation lifecycle via state-machine transitions on `PUT`. Automatically adjusts stock counters and generates R$ 2.00/day fines when overdue. Filterable by client, book, and status. |
| **Fines** | Fine registry linked to reservations. Filterable by reservation and status. |
| **Client History** | Per-client audit trail tracking field changes. |
| **Logs** | System-wide audit log capturing actions with timestamp and IP. |

### Reservation State Machine

```
reservado ──→ emprestado ──→ devolvido
    │              │
    ├── cancelado  └── atrasado ──→ devolvido
    └── devolvido              └── cancelado
```

Each transition adjusts stock counters atomically. The `atrasado` state auto-generates a fine.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+

> A PostgreSQL container is automatically provisioned via Quarkus Dev Services during development — no manual database setup required.

### Run

```bash
cd Backend
./mvnw quarkus:dev
```

### Build

```bash
./mvnw package
./mvnw package -Pnative   # Native executable (requires GraalVM)
```

### Testing

```bash
./mvnw test
./mvnw verify   # Includes integration tests (requires a running database or Dev Services)
```

---

## API Documentation

Once running, OpenAPI docs are available at:

- **Swagger UI:** [`http://localhost:8080/q/swagger-ui`](http://localhost:8080/q/swagger-ui)
- **OpenAPI spec:** [`http://localhost:8080/q/openapi`](http://localhost:8080/q/openapi)

### Health Checks

- **Health:** [`http://localhost:8080/q/health`](http://localhost:8080/q/health)
- **Liveness:** [`http://localhost:8080/q/health/live`](http://localhost:8080/q/health/live)
- **Readiness:** [`http://localhost:8080/q/health/ready`](http://localhost:8080/q/health/ready)

---

## Project Structure

```
Backend/
  pom.xml
  src/
    main/
      java/biblioteca/
        ├── autores/          # Author CRUD
        ├── clientes/         # Client CRUD + login
        ├── estoque/          # Stock CRUD + availability
        ├── fotos/            # Photo CRUD
        ├── funcionarios/     # Employee CRUD + login
        ├── generos/          # Genre CRUD
        ├── historico/        # Client history audit
        ├── livros/           # Book CRUD + search (authors & genres via JPA)
        ├── logs/             # System logs
        ├── movimentacao/     # Stock movements
        ├── multas/           # Fine CRUD
        └── reservas/         # Reservation CRUD + state machine
      resources/
        application.properties
        db/migration/         # Flyway V1–V8
  target/
  .mvn/
  mvnw
```

---

## License

Projeto acadêmico — Biblioteca.
