---
theme: default
layout: cover
title: Biblioteca - Sistema de Gerenciamento de Biblioteca
titleTemplate: '%s'
info: |
  ## Biblioteca - Sistema de Gerenciamento de Biblioteca
  Apresentação para a disciplina de Programação Orientada a Objetos
drawings:
  persist: false
transition: slide-left
mdc: true
lineNumbers: false
colorSchema: dark
canvasWidth: 980
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

# Sumário

<div class="grid grid-cols-2 gap-4 pt-4">

<div>

1. **Apresentação do Projeto**
2. **Motivação**
3. **Tecnologias**
4. **Diagrama de Classes**

</div>

<div>

5. **Funcionalidades Principais**
6. **Demonstração do Sistema**
7. **Conclusão**

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
zoom: 0.95
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
zoom: 1.05
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
zoom: 0.53
transition: slide-left
---

# Diagrama de Classes — Operações

```mermaid
classDiagram
    direction LR
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

**Fluxo demonstrado:**

1. **Autenticação** — login como cliente e como funcionário
2. **Catálogo** — navegação e busca de livros
3. **Reserva** — cliente realiza uma reserva
4. **Administrativo** — funcionário gerencia retirada e devolução
5. **Multas** — geração automática e visualização
6. **Estoque** — verificação dos contadores em tempo real

---
transition: slide-left
layout: center
---

# Conclusão

O projeto entregou um sistema funcional de biblioteca do começo ao fim — cadastro de livros, controle de estoque, reservas, devoluções e multas, tudo funcionando. Aprendemos na prática a organizar um projeto maior, separando bem as responsabilidades, e usamos tecnologias que são relevantes no mercado hoje. 

---
transition: fade-out
layout: center
---

# <span class="text-6xl">Obrigado!</span>



