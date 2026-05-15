---
theme: seriph
layout: cover
title: Biblioteca - Sistema de Gerenciamento de Biblioteca
titleTemplate: '%s'
info: |
  ## Biblioteca - Sistema de Gerenciamento de Biblioteca
  Apresentação para a disciplina de Programação Orientada a Objetos
drawings:
  persist: false
transition: fade-out
mdc: true
lineNumbers: false
---

# Biblioteca

**Sistema de Gerenciamento de Biblioteca**

Programação Orientada a Objetos

<div class="pt-4">
  <div class="text-sm opacity-70">Bruno Pereira de Souza · Fernando Petri · Jhonatan Rosendo</div>
  <div class="text-sm opacity-70">Jonathan Martins Melgar · Michelli Segantini</div>
</div>

---
transition: slide-left
---

# Agenda

<div class="grid grid-cols-2 gap-4 pt-4">

<div>

1. **Apresentação do Projeto**
2. **Motivação**
3. **Tecnologias**
4. **Arquitetura do Sistema**
5. **Diagrama de Classes**

</div>

<div>

6. **Funcionalidades Principais**
8. **Demonstração do Sistema**
9. **Conclusão**
10. **Perguntas**

</div>

</div>

---
transition: slide-left
layout: two-cols-header
---

# Apresentação do Projeto

::left::

<div class="pt-2">

**O que é?**

Sistema full-stack para gerenciamento de bibliotecas físicas, desenvolvido como projeto acadêmico na disciplina de POO.

**Propósito**

Automatizar o ciclo completo de uma biblioteca: cadastro de acervo, controle de estoque, reservas de livros, geração automática de multas e auditoria de operações.

</div>

::right::

<div class="pt-2">

**Perfis de Acesso**

- **Clientes** — consultam o catálogo e realizam reservas
- **Funcionários** — gerenciam as operações da biblioteca (Admin, Gerente, Staff)

**Escopo**

Backend (API REST) + Frontend (SPA React) + Banco de Dados (PostgreSQL)

</div>

---
transition: slide-left
layout: two-cols-header
---

# Motivação

::left::

<div class="pt-2">

**Problema**

Muitas bibliotecas físicas ainda operam com sistemas legados, planilhas ou controle manual, resultando em:

- Ineficiência operacional
- Dificuldade no rastreamento de reservas e devoluções
- Controle de estoque impreciso
- Processo manual de cobrança de multas

</div>

::right::

<div class="pt-2">

**Solução**

Criar uma aplicação modernizada que unifica:

- Cadastro centralizado de livros, autores e gêneros
- Controle de estoque em tempo real
- Sistema de reservas com máquina de estados
- Geração automática de multas por atraso
- Histórico e logs de auditoria
- Autenticação JWT com dois perfis de acesso

</div>

---
transition: slide-left
---

# Tecnologias

<div class="grid grid-cols-3 gap-8 pt-4">

<div>

### Backend

| Tecnologia | Versão |
|------------|--------|
| Java | 21 |
| Quarkus | 3.32 |
| Hibernate Panache | — |
| PostgreSQL | — |
| Flyway | — |
| Maven | — |
| SmallRye OpenAPI | — |
| SmallRye JWT | — |

</div>

<div>

### Frontend

| Tecnologia | Versão |
|------------|--------|
| React | 19 |
| Vite | 7 |
| Tailwind CSS | 4 |
| React Router | 7 |
| Axios | — |
| FontAwesome | 7 |
| ESLint | 9 |

</div>

<div>

### Ferramentas

| Tecnologia | Uso |
|------------|-----|
| Git | Controle de versão |
| GitHub | Repositório remoto |
| Swagger UI | Documentação da API |
| Maven Wrapper | Build padronizado |
| Flyway | Migrations |

</div>

</div>

---
zoom: 0.75
transition: slide-left
---

# Arquitetura do Sistema

```mermaid
flowchart LR
    subgraph Frontend["Frontend (React SPA)"]
        CD["Client Dashboard"]
        AD["Admin Dashboard"]
        AR["Auth & Routing"]
    end

    subgraph Backend["Backend (Quarkus REST API)"]
        direction TB
        C["Controllers (REST)"]
        S["Services<br/>(TokenService, LogService)"]
        R["Repositories<br/>(Panache)"]
        E["Entities (JPA)"]
        DTO["DTOs"]
        E --> R --> C
        S -.-> C
        C -.-> DTO
    end

    subgraph DB["Database"]
        PG[("PostgreSQL<br/>12 tabelas")]
        FM["Flyway Migrations<br/>V1–V10 + SEED"]
    end

    Frontend -- "HTTP/JSON · JWT" --> Backend
    Backend -- "JDBC" --> DB
```

---
zoom: 0.65
transition: slide-left
---

# Diagrama de Classes — Acervo

```mermaid
classDiagram
    class Livro {
        +Integer idLivro
        +String nomeLivro
        +String isnb
        +String editora
        +Integer ano
        +String sinopse
        +String localizacaoFisica
    }
    class Autor {
        +Integer idAutor
        +String nomeAutor
    }
    class Genero {
        +Integer idGenero
        +String nomeGenero
    }
    class Estoque {
        +Integer idEstoque
        +Integer quantidadeTotal
        +Integer quantidadeReservada
        +Integer quantidadeEmprestada
        +Integer quantidadeDanificada
        +Integer estoqueMinimo
    }
    class Foto {
        +Integer idFoto
        +String foto
    }
    class MovimentacaoEstoque {
        +Integer idMovimentacao
        +Character tipoMovimentacao
        +Integer quantidade
        +LocalDate dataMovimentacao
        +String motivo
    }

    Livro "*" --> "*" Autor
    Livro "*" --> "*" Genero
    Livro "1" --> "1" Estoque
    Livro "1" --> "*" Foto
    Livro "1" --> "*" MovimentacaoEstoque
```

---
zoom: 0.5
transition: slide-left
---

# Diagrama de Classes — Operações

```mermaid
classDiagram
    class Reserva {
        +Integer idReserva
        +LocalDate dataReserva
        +LocalDate dataLimiteRetirada
        +LocalDate dataRetirada
        +LocalDate dataPrevistaDevolucao
        +LocalDate dataDevolucao
        +String statusReserva
        +String codigoReserva
    }
    class Multa {
        +Integer idMulta
        +BigDecimal valorMulta
        +LocalDate dataMulta
        +String statusMulta
        +LocalDate dataPagamento
    }
    class Cliente {
        +Integer idCliente
        +String nomeCliente
        +String cpf
        +LocalDate dataNascimento
        +String telefone
        +String email
        +String endereco
        +Boolean bloqueado
        +Integer tentativasLogin
    }
    class Funcionario {
        +Integer idFuncionario
        +String nome
        +String email
        +Character perfil
    }
    class HistoricoCliente {
        +Integer idHistorico
        +String campoAlterado
        +String valorAntigo
        +String valorNovo
        +LocalDate dataAlteracao
    }
    class Log {
        +Integer idLog
        +String acao
        +LocalDateTime dataAcao
        +String ip
    }
    class Livro {
        +Integer idLivro
        +String nomeLivro
        +String isnb
        +String editora
        +Integer ano
        +String sinopse
        +String localizacaoFisica
    }

    Livro "1" --> "*" Reserva
    Cliente "1" --> "*" HistoricoCliente
    Cliente "1" --> "*" Reserva
    Reserva "1" --> "*" Multa
    Reserva "*" --> "1" Funcionario : retirada
    Reserva "*" --> "1" Funcionario : devolucao
    Cliente "1" --> "*" Log
    Funcionario "1" --> "*" Log
```

---
transition: slide-left
---

# Funcionalidades Principais

- **Autenticação JWT** com BCrypt e proteção contra brute-force (bloqueio após 5 tentativas)
- **Perfis de acesso**: Admin (A), Gerente (G), Staff (F), Cliente \(C\)
- **Controle de estoque** com contadores atualizados atomicamente a cada transação
- **Geração automática de multas** vinculadas ao status da reserva
- **Auditoria**: Logs de todas as operações e histórico de alterações de clientes
- **Documentação automática da API** via Swagger UI
- **Validação de dados** com Hibernate Validator
- **CRUD genérico** no frontend administrativo

---
transition: slide-left
---

# Demonstração do Sistema

<div class="pt-4">

## Sistema em Funcionamento

A demonstração será apresentada ao vivo com o sistema rodando localmente.

**Fluxo demonstrado:**

1. **Autenticação** — login como cliente e como funcionário
2. **Catálogo** — navegação e busca de livros
3. **Reserva** — cliente realiza uma reserva
4. **Administrativo** — funcionário gerencia retirada e devolução
5. **Multas** — geração automática e visualização
6. **Estoque** — verificação dos contadores em tempo real

</div>

---
transition: slide-left
---

# Conclusão

<div class="grid grid-cols-2 gap-8 pt-4">

<div>

### O que foi aprendido

- **POO na prática** — encapsulamento, herança (PanacheEntityBase), relacionamentos JPA
- **Arquitetura em camadas** — separação clara entre Entity, Repository, Controller e DTO
- **Padrões de projeto** — Repository Pattern, State Machine, DTO, Injeção de Dependência
- **Tecnologias modernas** — Java 21, Quarkus, React 19, JWT

</div>

<div>

### Resultados

- Sistema funcional com **54 classes Java** organizadas em **13 módulos**
- **12 tabelas** no banco de dados gerenciadas via Flyway
- **2 interfaces** (cliente e administrativo) em React
- API documentada automaticamente com Swagger
- Código disponível no GitHub

</div>

</div>

---
transition: fade-out
layout: center
---

# Obrigado!

<div class="text-center pt-4">

**Repositório:** [github.com/anomalyco/Biblioteca](https://github.com/anomalyco/Biblioteca)

</div>

