# Auth JWT — Implementation Plan

## Summary

Replace plain-text password authentication with JWT-based stateless auth, add BCrypt password hashing, implement role-based access control with four authorization levels (cliente, funcionario, gerente, admin), and update the frontend to use bearer tokens.

---

## Actors & Roles

| Actor | JWT Group | Entity | Profile |
|---|---|---|---|
| Guest | — (no token) | — | Unauthenticated browser |
| Client | `cliente` | `Clientes` | Regular library user |
| Staff | `funcionario` | `Funcionarios` | perfil = `F` (Funcionário) |
| Manager | `gerente` | `Funcionarios` | perfil = `G` (Gerente) |
| Admin | `admin` | `Funcionarios` | perfil = `A` (Administrador) |

Group hierarchy: `admin` > `gerente` > `funcionario` > `cliente` > guest. A higher group inherits permissions from lower groups.

---

## Entities (New)

### `auth` package — new module at `biblioteca/auth/`

| File | Responsibility |
|---|---|
| `biblioteca/auth/controller/AuthController.java` | Unified login + current-user endpoint |
| `biblioteca/auth/TokenService.java` | JWT generation, credential validation, BCrypt verification |
| `biblioteca/auth/models/LoginRequest.java` | Request DTO: `{ email, senha, tipo }` |
| `biblioteca/auth/models/LoginResponse.java` | Response DTO: `{ token, user: { id, name, email, tipo, perfil } }` |

### Token claims structure

```json
{
  "sub": "cliente@biblioteca.com",
  "upn": "cliente@biblioteca.com",
  "groups": ["cliente"],
  "userId": 1,
  "userType": "cliente",
  "perfil": null,
  "iat": 1715347200,
  "exp": 1715433600
}
```

Staff tokens:
```json
{
  "sub": "admin@biblioteca.com",
  "upn": "admin@biblioteca.com",
  "groups": ["admin"],
  "userId": 1,
  "userType": "funcionario",
  "perfil": "A",
  "iat": 1715347200,
  "exp": 1715433600
}
```

Group mapping rule: `cliente` → `["cliente"]`, funcionario perfil `F` → `["funcionario"]`, perfil `G` → `["gerente"]`, perfil `A` → `["admin"]`.

---

## Architecture Decision Records

### ADR-001: Custom JWT over Quarkus Security JPA

**Context:** Quarkus offers two main auth paths: (a) Security JPA (`@UserDefinition`, `@Password`, `@Roles`) which handles auth via the container, and (b) SmallRye JWT with custom token generation. The project has two distinct user entities (`Clientes` and `Funcionarios`) in separate tables, which the JPA security model does not support natively (it expects a single user table).

**Decision:** Use `quarkus-smallrye-jwt` + `quarkus-smallrye-jwt-build` with a custom `TokenService` that bridges both entity types. This keeps the login unified and avoids forcing two distinct user types into a single table or view.

**Consequences:** + Works with existing schema, + No schema changes needed beyond column size, – Manual token generation and validation logic is required.

### ADR-002: RSA key pair for JWT signing

**Context:** SmallRye JWT supports both RSA key pairs and symmetric HMAC secrets. Symmetric keys are simpler but require the signing secret to be shared with every verifying service. The API is monolithic (single service), so this is less of a concern, but RSA aligns with specification defaults and future-proofs for potential service splitting.

**Decision:** Generate an RSA-2048 key pair. Store the private key for signing (`privatekey.pem`) and public key for verification (`publickey.pem`) in `src/main/resources/`. Token expiry: 24 hours.

**Consequences:** + Standard practice, + Future-proof, – Extra key generation step in setup.

### ADR-003: BCrypt via Quarkus Elytron over external libraries

**Context:** Several Java BCrypt libraries exist (jBCrypt, Spring Security Crypto). Quarkus ships `BcryptUtil` in `quarkus-elytron-security-common` which delegates to WildFly Elytron's `PasswordFactory`.

**Decision:** Use `BcryptUtil.bcryptHash()` for hashing and WildFly Elytron's `PasswordFactory` for verification. Zero additional dependencies beyond the Quarkus extension.

**Consequences:** + No extra dependency, + Quarkus-native, – Verification requires a few more lines of code (PasswordFactory boilerplate).

### ADR-004: Unified `/auth/login` over separate login endpoints

**Context:** The current codebase has `POST /clientes/login` and `POST /funcionarios/login` that return the full entity DTO (including internal IDs and unnecessary fields). The frontend already distinguishes login type via a tab selector.

**Decision:** Create `POST /auth/login` that accepts `{ email, senha, tipo }` and returns `{ token, user }`. The old login endpoints are deprecated but retained temporarily for backward compatibility (removed in a follow-up).

**Consequences:** + Single auth entry point, + Frontend sends `tipo` it already knows, – Two login endpoints remain temporarily.

---

## Endpoint Access Matrix

Legend: ✅ = allowed, — = denied, 🔒(own) = can access own records only.

| # | Method | Path | Guest | Cliente | Func. | Gerente | Admin |
|---|---|---|---|---|---|---|---|
| | **Auth** | | | | | | |
| 1 | POST | `/auth/login` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 2 | GET | `/auth/me` | — | ✅ | ✅ | ✅ | ✅ |
| | **Books** | | | | | | |
| 3 | GET | `/livros` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 4 | GET | `/livros/{id}` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 5 | GET | `/livros/{id}/autores` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 6 | GET | `/livros/{id}/generos` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 7 | POST | `/livros` | — | — | — | ✅ | ✅ |
| 8 | POST | `/livros/{id}/autores` | — | — | — | ✅ | ✅ |
| 9 | DELETE | `/livros/{id}/autores/{idA}` | — | — | — | ✅ | ✅ |
| 10 | POST | `/livros/{id}/generos` | — | — | — | ✅ | ✅ |
| 11 | DELETE | `/livros/{id}/generos/{idG}` | — | — | — | ✅ | ✅ |
| 12 | PUT | `/livros/{id}` | — | — | — | ✅ | ✅ |
| 13 | DELETE | `/livros/{id}` | — | — | — | — | ✅ |
| | **Authors** | | | | | | |
| 14 | GET | `/autores` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 15 | GET | `/autores/{id}` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 16 | GET | `/autores/{id}/livros` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 17 | POST | `/autores` | — | — | — | ✅ | ✅ |
| 18 | PUT | `/autores/{id}` | — | — | — | ✅ | ✅ |
| 19 | DELETE | `/autores/{id}` | — | — | — | — | ✅ |
| | **Genres** | | | | | | |
| 20 | GET | `/generos` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 21 | GET | `/generos/{id}` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 22 | GET | `/generos/{id}/livros` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 23 | POST | `/generos` | — | — | — | ✅ | ✅ |
| 24 | PUT | `/generos/{id}` | — | — | — | ✅ | ✅ |
| 25 | DELETE | `/generos/{id}` | — | — | — | — | ✅ |
| | **Photos** | | | | | | |
| 26 | GET | `/fotos` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 27 | GET | `/fotos/{id}` | ✅ | ✅ | ✅ | ✅ | ✅ |
| 28 | POST | `/fotos` | — | — | — | ✅ | ✅ |
| 29 | PUT | `/fotos/{id}` | — | — | — | ✅ | ✅ |
| 30 | DELETE | `/fotos/{id}` | — | — | — | — | ✅ |
| | **Clients** | | | | | | |
| 31 | GET | `/clientes` | — | — | — | — | ✅ |
| 32 | GET | `/clientes/{id}` | — | 🔒(own) | — | — | ✅ |
| 33 | POST | `/clientes` | — | — | — | ✅ | ✅ |
| 34 | PUT | `/clientes/{id}` | — | 🔒(own) | — | — | ✅ |
| 35 | DELETE | `/clientes/{id}` | — | — | — | — | ✅ |
| | **Employees** | | | | | | |
| 36 | GET | `/funcionarios` | — | — | — | — | ✅ |
| 37 | GET | `/funcionarios/{id}` | — | — | — | — | ✅ |
| 38 | POST | `/funcionarios` | — | — | — | — | ✅ |
| 39 | PUT | `/funcionarios/{id}` | — | — | — | — | ✅ |
| 40 | DELETE | `/funcionarios/{id}` | — | — | — | — | ✅ |
| | **Stock** | | | | | | |
| 41 | GET | `/estoque` | — | — | ✅ | ✅ | ✅ |
| 42 | GET | `/estoque/{id}` | — | — | ✅ | ✅ | ✅ |
| 43 | POST | `/estoque` | — | — | — | ✅ | ✅ |
| 44 | PUT | `/estoque/{id}` | — | — | — | ✅ | ✅ |
| 45 | DELETE | `/estoque/{id}` | — | — | — | — | ✅ |
| | **Stock Movements** | | | | | | |
| 46 | GET | `/movimentacao` | — | — | ✅ | ✅ | ✅ |
| 47 | GET | `/movimentacao/{id}` | — | — | ✅ | ✅ | ✅ |
| 48 | POST | `/movimentacao` | — | — | ✅ | ✅ | ✅ |
| 49 | DELETE | `/movimentacao/{id}` | — | — | — | — | ✅ |
| | **Reservations** | | | | | | |
| 50 | GET | `/reservas` | — | 🔒(own) | ✅ | ✅ | ✅ |
| 51 | GET | `/reservas/{id}` | — | 🔒(own) | ✅ | ✅ | ✅ |
| 52 | POST | `/reservas` | — | ✅ | ✅ | ✅ | ✅ |
| 53 | PUT | `/reservas/{id}` | — | — | ✅ | ✅ | ✅ |
| 54 | DELETE | `/reservas/{id}` | — | — | — | — | ✅ |
| | **Fines** | | | | | | |
| 55 | GET | `/multas` | — | 🔒(own) | ✅ | ✅ | ✅ |
| 56 | GET | `/multas/{id}` | — | 🔒(own) | ✅ | ✅ | ✅ |
| 57 | POST | `/multas` | — | — | — | ✅ | ✅ |
| 58 | PUT | `/multas/{id}` | — | — | — | ✅ | ✅ |
| 59 | DELETE | `/multas/{id}` | — | — | — | — | ✅ |
| | **Logs** | | | | | | |
| 60 | GET | `/logs` | — | — | — | — | ✅ |
| 61 | GET | `/logs/{id}` | — | — | — | — | ✅ |
| 62 | DELETE | `/logs/{id}` | — | — | — | — | ✅ |
| | **Client History** | | | | | | |
| 63 | GET | `/historico` | — | — | — | — | ✅ |
| 64 | GET | `/historico/{id}` | — | — | — | — | ✅ |

**Ownership rule** (🔒(own)): A cliente can access their own records. The controller checks that `jwt.getClaim("userId")` matches the resource's `idCliente` field, and that `jwt.getClaim("userType")` equals `"cliente"`.

---

## Implementation Steps

### Step 1 — Add dependencies to `pom.xml`

```xml
<!-- JWT authentication -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt-build</artifactId>
</dependency>
<!-- BCrypt password hashing -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-elytron-security-common</artifactId>
</dependency>
```

### Step 2 — Generate RSA key pair

Generate an RSA-2048 private key and extract the public key:

```bash
# Generate private key
openssl genrsa -out src/main/resources/privatekey.pem 2048
# Extract public key
openssl rsa -in src/main/resources/privatekey.pem -pubout -out src/main/resources/publickey.pem
```

### Step 3 — Configure `application.properties`

```properties
# JWT configuration
smallrye.jwt.sign.key.location=/privatekey.pem
mp.jwt.verify.publickey.location=/publickey.pem
mp.jwt.verify.issuer=biblioteca
smallrye.jwt.new-token-lifespan=86400

# Ensure CORS allows Authorization header (already present, verify)
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
```

**Important:** When `quarkus-smallrye-jwt` is added, Quarkus secures ALL endpoints by default (equivalent to `@DenyAll` on every method). Every single JAX-RS method across all controllers MUST have an explicit security annotation (`@PermitAll`, `@RolesAllowed`, or `@Authenticated`). Failing to annotate a method will cause it to return 401/403 regardless of authentication.

**Important:** Any controller class that injects `JsonWebToken jwt` MUST be annotated with `@RequestScoped` (from `jakarta.enterprise.context.RequestScoped`). This is a requirement from Quarkus/SmallRye for CDI proxy creation.

### Step 4 — Create `biblioteca/auth/TokenService.java`

```java
package biblioteca.auth;

import biblioteca.clientes.data.Cliente;
import biblioteca.clientes.repository.RepositorioClientes;
import biblioteca.funcionarios.data.Funcionario;
import biblioteca.funcionarios.repository.RepositorioFuncionarios;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claims;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    @Inject
    RepositorioClientes repositorioClientes;

    @Inject
    RepositorioFuncionarios repositorioFuncionarios;

    public LoginResponse authenticate(String email, String senha, String tipo) {
        if ("cliente".equals(tipo)) {
            Cliente cliente = repositorioClientes.find("email", email).firstResult();
            if (cliente == null) return null;
            if (Boolean.TRUE.equals(cliente.getBloqueado())) {
                throw new AuthException("Conta bloqueada por excesso de tentativas.");
            }
            if (!verifyPassword(senha, cliente.getSenhaCliente())) {
                int tentativas = cliente.getTentativasLogin() + 1;
                cliente.setTentativasLogin(tentativas);
                if (tentativas >= 5) {
                    cliente.setBloqueado(true);
                }
                repositorioClientes.persist(cliente);
                return null;  // caller handles 401
            }
            // success — reset attempts
            cliente.setTentativasLogin(0);
            repositorioClientes.persist(cliente);

            String token = generateToken(cliente.getIdCliente(), cliente.getEmail(), "cliente", null);
            return new LoginResponse(token, cliente.getIdCliente(), cliente.getNomeCliente(), cliente.getEmail(), "cliente", null);

        } else if ("funcionario".equals(tipo)) {
            Funcionario func = repositorioFuncionarios.find("email", email).firstResult();
            if (func == null) return null;
            if (!verifyPassword(senha, func.getSenha())) return null;

            String perfilStr = String.valueOf(func.getPerfil());
            String group = mapPerfilToGroup(func.getPerfil());
            String token = generateToken(func.getIdFuncionario(), func.getEmail(), "funcionario", perfilStr);
            return new LoginResponse(token, func.getIdFuncionario(), func.getNome(), func.getEmail(), "funcionario", perfilStr);
        }

        return null;
    }

    private String generateToken(Integer userId, String email, String userType, String perfil) {
        String group;
        if ("cliente".equals(userType)) {
            group = "cliente";
        } else {
            group = mapPerfilToGroup(perfil != null ? perfil.charAt(0) : 'F');
        }

        return Jwt.issuer("biblioteca")
                .upn(email)
                .groups(new HashSet<>(Set.of(group)))
                .claim("userId", userId)
                .claim("userType", userType)
                .claim("perfil", perfil)
                .sign();
    }

    private String mapPerfilToGroup(char perfil) {
        return switch (perfil) {
            case 'A' -> "admin";
            case 'G' -> "gerente";
            case 'F' -> "funcionario";
            default -> "funcionario";
        };
    }

    private boolean verifyPassword(String plain, String storedHash) {
        try {
            PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);
            BCryptPassword entry = (BCryptPassword) ModularCrypt.parse(storedHash);
            return factory.verify(entry, plain.toCharArray());
        } catch (Exception e) {
            return false;
        }
    }

    public static String hashPassword(String plain) {
        return BcryptUtil.bcryptHash(plain);
    }
}
```

### Step 5 — Create `AuthController.java`

```java
package biblioteca.auth.controller;

import biblioteca.auth.TokenService;
import biblioteca.auth.models.LoginRequest;
import biblioteca.auth.models.LoginResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest request) {
        try {
            LoginResponse response = tokenService.authenticate(request.getEmail(), request.getSenha(), request.getTipo());
            if (response == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Credenciais inválidas ou conta bloqueada.")
                        .build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/me")
    @Authenticated  // any authenticated user (all groups)
    public Response me() {
        return Response.ok(new java.util.HashMap<>(java.util.Map.of(
            "userId", jwt.getClaim("userId"),
            "email", jwt.getClaim("upn"),
            "userType", jwt.getClaim("userType"),
            "perfil", jwt.getClaim("perfil"),
            "groups", jwt.getGroups()
        ))).build();
    }
}
```

### Step 6 — Create DTOs

**`LoginRequest.java`:**
```java
package biblioteca.auth.models;

public class LoginRequest {
    private String email;
    private String senha;
    private String tipo; // "cliente" or "funcionario"

    // getters and setters
}
```

**`LoginResponse.java`:**
```java
package biblioteca.auth.models;

public class LoginResponse {
    private String token;
    private Integer userId;
    private String name;
    private String email;
    private String tipo;
    private String perfil;

    // constructor, getters and setters
}
```

**`AuthException.java`** (in `biblioteca/auth/`):
```java
package biblioteca.auth;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
```

### Step 7 — Flyway V9: increase password column sizes and migrate passwords

Create `V9__Auth_jwt_password_hashing.sql`:

```sql
-- Increase password column sizes to fit BCrypt hashes (60 chars for BCrypt)
ALTER TABLE Clientes ALTER COLUMN senha_cliente TYPE VARCHAR(255);
ALTER TABLE Funcionarios ALTER COLUMN senha TYPE VARCHAR(255);

-- Note: Existing passwords are plain text ('123') and need to be migrated.
-- Since we cannot reverse BCrypt, the migration script will UPDATE with
-- pre-computed BCrypt hashes of known seed passwords.
-- The BCrypt hash of '123' with default cost 10 is a known constant.
-- This will be generated at implementation time with a one-time script.
```

**Important:** The BCrypt hash of `"123"` needs to be computed at implementation time (it changes each time due to random salt). The migration will use a deterministic value computed once during implementation.

### Step 8 — Update entity creation endpoints to hash passwords

**In `ClienteController.java`:**
- In `criar()`: hash `clienteDTO.getSenhaCliente()` with `TokenService.hashPassword()` before persisting.
- In `atualizar()`: only rehash if `senhaCliente` is present and non-empty in the DTO.

**In `FuncionarioController.java`:**
- In `criar()`: hash `funcionarioDTO.getSenha()` with `TokenService.hashPassword()` before persisting.
- In `atualizar()`: only rehash if `senha` is present and non-empty in the DTO.

### Step 9 — Add security annotations to all controllers

Apply `@jakarta.annotation.security.PermitAll` and `@jakarta.annotation.security.RolesAllowed` annotations according to the access matrix above.

**Implementation strategy per controller:**

For each controller class, add `@RequestScoped` (needed to inject `JsonWebToken`) and inject the JWT token. Then annotate each method.

Example pattern for a controller with mixed access:

```java
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/livros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped           // ← required for JsonWebToken injection
public class LivroController {

    @Inject
    JsonWebToken jwt;    // ← injected JWT for ownership checks

    @GET
    @PermitAll
    public Response listarTodos(...) { ... }

    @POST
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response criar(...) { ... }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(...) { ... }
}
```

**Ownership checks** (🔒(own) in matrix):

For endpoints where a client can only access their own data (e.g., `GET /clientes/{id}`, `GET /reservas`, `GET /multas`):

```java
@GET
@Path("/{id}")
@RolesAllowed({"cliente", "admin"})
public Response buscarPorId(@PathParam("id") Integer id) {
    String userType = jwt.getClaim("userType");
    Integer userId = Integer.valueOf(jwt.getClaim("userId").toString());

    // Client can only view their own profile; admin can view any
    if ("cliente".equals(userType) && !userId.equals(id)) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }
    ...
}
```

**List filter for own resources** (e.g., `GET /reservas` for a client):

```java
@GET
@RolesAllowed({"cliente", "funcionario", "gerente", "admin"})
public Response listarTodos(
        @QueryParam("idCliente") Integer idCliente,
        @QueryParam("idLivro") Integer idLivro,
        @QueryParam("status") String status) {

    String userType = jwt.getClaim("userType");
    // Force filter to own client ID if user is a cliente
    if ("cliente".equals(userType)) {
        idCliente = Integer.valueOf(jwt.getClaim("userId").toString());
    }
    ...
}
```

### Step 10 — Fix seed data perfil value

In `V3__Insert_test_users.sql`, atendente's perfil is `'U'` which doesn't match the documented options. Either:
- Fix the seed to use `'F'` (Funcionário) instead of `'U'`, OR
- Add a new Flyway migration `V10__Fix_atendente_perfil.sql`:
  ```sql
  UPDATE Funcionarios SET perfil = 'F' WHERE perfil = 'U';
  ```

### Step 11 — Update Frontend: `apiClient.js`

Add request interceptor to inject Bearer token:

```js
import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor — attach JWT token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("biblioteca-token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor — handle 401
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("biblioteca-token");
      localStorage.removeItem("biblioteca-user");
      window.location.href = "/";
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### Step 12 — Update Frontend: `authService.js`

Replace the old login calls with the unified `/auth/login` endpoint:

```js
import apiClient from "./apiClient";

export async function login(email, senha, tipo) {
  try {
    const response = await apiClient.post("/auth/login", { email, senha, tipo });
    return response.data; // { token, userId, name, email, tipo, perfil }
  } catch (error) {
    const backendMessage = error.response?.data;
    if (typeof backendMessage === "string" && backendMessage.trim()) {
      throw new Error(backendMessage);
    }
    throw new Error("Falha ao autenticar. Verifique suas credenciais.");
  }
}
```

### Step 13 — Update Frontend: `AuthProvider.jsx`

Properly store and manage the JWT token:

```jsx
import { useState, useEffect } from "react";
import { AuthContext } from "./AuthContext";
import { login as loginService } from "../services/authService";

export function AuthProvider({ children }) {
  const STORAGE_KEY = "biblioteca-user";
  const TOKEN_KEY = "biblioteca-token";

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const rawUser = localStorage.getItem(STORAGE_KEY);
    const token = localStorage.getItem(TOKEN_KEY);
    if (rawUser && token) {
      try {
        setUser(JSON.parse(rawUser));
      } catch {
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem(TOKEN_KEY);
      }
    }
    setLoading(false);
  }, []);

  async function handleLogin(email, password, tipo) {
    const data = await loginService(email, password, tipo);

    const role = tipo === "funcionario" ? "ADMIN" : "USER";

    const currentUser = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role,
      tipo: data.tipo,
      perfil: data.perfil,
      raw: data,
    };

    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(currentUser));
    setUser(currentUser);
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(TOKEN_KEY);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, handleLogin, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}
```

### Step 14 — Update Frontend: `Login.jsx`

Update to pass login type to the new handler:

```jsx
// In the submit handler:
await handleLogin(email, password, loginType);
// loginType is "cliente" or "funcionario" (already set by the tabs)
```

### Step 15 — Update Frontend: `ProtectedRoute.jsx`

No changes needed — the existing role check (`user.role === "ADMIN"` / `"USER"`) already maps correctly since the AuthProvider now maps `funcionario` → `"ADMIN"` and `cliente` → `"USER"`.

### Step 16 — Update Frontend: `AdminDashboard.jsx`

Add token check on page load. If the token is missing or expired, redirect to login. This complements the existing route guard.

### Step 17 — Update Frontend: `Dashboard.jsx` (client)

The existing client dashboard calls API endpoints directly via `apiClient`. Since the interceptor now injects the Bearer token, these calls will automatically be authenticated. The key changes:
- `POST /clientes/login` calls are replaced with the new auth flow (already done in Step 12-13).
- The reservation creation (`POST /reservas`) will now work correctly with the authenticated user.

---

## Open Questions

1. **Old login endpoints**: Should `POST /clientes/login` and `POST /funcionarios/login` be removed, or kept for backward compatibility with a deprecation notice? **Recommendation:** Keep them but have them delegate to the new TokenService, returning a JWT instead of the entity DTO. Remove in a follow-up feature.

2. **Seed data perfil**: The existing seed has perfil `'U'` for `atendente@biblioteca.com`, which is not a documented value (`A`, `G`, `F`). Should this be changed to `'F'`? **Recommendation:** Yes — fix in a migration.

3. **Public book browsing**: The current frontend shows the book catalog on the client dashboard (after login). Should book browsing (`GET /livros`) remain completely public (no token required) so that even unauthenticated users can browse? **Recommendation:** Yes, keep it public for discoverability. The client login is needed only for reservations and fine tracking.

4. **Client registration**: Should there be a public `POST /clientes` endpoint for self-registration? Currently it's admin-only. **Recommendation:** Out of scope for this feature. Current behavior (admin creates clients) remains.

---

## Files Changed Summary

### Backend — New files
| File | Purpose |
|---|---|
| `src/main/java/biblioteca/auth/controller/AuthController.java` | Unified auth endpoints |
| `src/main/java/biblioteca/auth/TokenService.java` | JWT generation + password verification |
| `src/main/java/biblioteca/auth/AuthException.java` | Authentication exception |
| `src/main/java/biblioteca/auth/models/LoginRequest.java` | Login request DTO |
| `src/main/java/biblioteca/auth/models/LoginResponse.java` | Login response DTO |
| `src/main/resources/privatekey.pem` | RSA private key for JWT signing |
| `src/main/resources/publickey.pem` | RSA public key for JWT verification |
| `src/main/resources/db/migration/V9__Auth_jwt_password_hashing.sql` | Schema migration |

### Backend — Modified files
| File | Change |
|---|---|
| `pom.xml` | Add 3 JWT + BCrypt dependencies |
| `src/main/resources/application.properties` | Add JWT config |
| `.../clientes/controller/ClienteController.java` | Hash password on create/update, add security annotations + ownership check |
| `.../funcionarios/controller/FuncionarioController.java` | Hash password on create/update, add security annotations |
| `.../livros/controller/LivroController.java` | Add security annotations |
| `.../autores/controller/AutorController.java` | Add security annotations |
| `.../generos/controller/GeneroController.java` | Add security annotations |
| `.../fotos/controller/FotoController.java` | Add security annotations |
| `.../estoque/controller/EstoqueController.java` | Add security annotations |
| `.../movimentacao/controller/MovimentacaoEstoqueController.java` | Add security annotations |
| `.../reservas/controller/ReservaController.java` | Add security annotations + ownership check |
| `.../multas/controller/MultaController.java` | Add security annotations + ownership check |
| `.../logs/controller/LogController.java` | Add security annotations |
| `.../historico/controller/HistoricoClienteController.java` | Add security annotations |
| `src/main/resources/db/migration/V3__Insert_test_users.sql` | (optional) Fix perfil `'U'` → `'F'` |

### Frontend — Modified files
| File | Change |
|---|---|
| `src/services/apiClient.js` | Add Bearer token interceptor + 401 handler |
| `src/services/authService.js` | Replace with new `/auth/login` endpoint |
| `src/context/AuthProvider.jsx` | Store JWT token, map funcionario → ADMIN role |
| `src/pages/Login.jsx` | Pass login type to handler (minimal change) |
