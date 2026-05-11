# Biblioteca — Frontend

[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react)](https://react.dev)
[![Vite](https://img.shields.io/badge/Vite-7-646CFF?logo=vite)](https://vite.dev)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?logo=tailwindcss)](https://tailwindcss.com)
[![React Router](https://img.shields.io/badge/React_Router-7-CA4245?logo=reactrouter)](https://reactrouter.com)

Client-facing and administrative SPA for the Biblioteca library management system. Built with **React 19**, **Vite**, and **Tailwind CSS 4**, it connects to the Quarkus REST API to provide book browsing, reservations, fine tracking, and full admin CRUD.

---

## Technologies

| Layer        | Stack |
| ------------ | ----- |
| Framework    | React 19 |
| Build tool   | Vite 7 |
| Styling      | Tailwind CSS 4 |
| Routing      | React Router 7 |
| HTTP client  | Axios |
| Icons        | FontAwesome 7 |
| Linting      | ESLint 9 |

---

## Features

### Client Dashboard

- Browse the full book catalog with cover images, author and genre info, and real-time availability.
- View personal reservation history and current status.
- Check outstanding fines.

### Admin Dashboard

A unified CRUD interface covering all backend resources, organized into four groups:

| Group | Resources |
| ----- | --------- |
| **Acervo** | Books, Authors, Genres, Photos, Book-Author, Book-Genre |
| **Pessoas** | Clients, Employees |
| **Operações** | Stock, Stock Movements, Reservations, Fines |
| **Sistema** | Logs, Client History |

Each resource supports listing, searching, creating, editing, and deleting.

### Authentication

- Two login flows: **Client** and **Employee**.
- Session persisted across page reloads.
- Route guards redirect unauthenticated users to the login screen.
- Role-based access separates client and admin areas.

---

## Architecture

```
src/
  components/       Reusable UI components (layout shell, shared widgets)
  pages/            Top-level views (login, client dashboard, admin dashboard)
  routes/           Route guards for authentication and role verification
  context/          Auth state management (login, logout, session persistence)
  services/         API communication layer (HTTP client, auth calls, generic CRUD)
  config/           Declarative resource definitions that drive the admin CRUD UI
```

### Key Concepts

**Generic CRUD** — The admin dashboard uses a single generic panel driven by declarative resource configurations. Each backend entity is defined once with its fields, types, and API endpoint, and the UI renders forms, tables, and filters automatically.

**Auth Context** — A context provider wraps the application and exposes the authenticated user and login/logout actions to any component.

**Protected Routes** — Route-level guards check authentication and role before rendering pages, redirecting unauthenticated users as needed.

---

## Getting Started

### Prerequisites

- Node.js 20+
- npm 10+
- Backend API running

### Environment

Create a `.env` file in the `Frontend/` root (optional — defaults to `http://localhost:8080`):

```env
VITE_API_URL=http://localhost:8080
```

### Install & Run

```bash
cd Frontend
npm install
npm run dev
```

Opens with hot-reload.

### Build

```bash
npm run build
npm run preview
```

### Lint

```bash
npm run lint
```

---

## Project Structure

```
Frontend/
  index.html
  package.json
  vite.config.js
  eslint.config.js
  public/
  src/
    components/
    pages/
    routes/
    context/
    services/
    config/
```

---

## License

Projeto acadêmico — Biblioteca.
