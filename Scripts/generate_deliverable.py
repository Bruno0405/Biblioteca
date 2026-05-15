#!/usr/bin/env python3
"""
Deliverable generator for Biblioteca OOP project.
Produces a Markdown document with:
- Mermaid class diagram (entities only, DB-focused)
- Full source code of all Java classes
"""

import os
import re
from pathlib import Path

BACKEND_SRC = Path("Backend/src/main/java/biblioteca")
OUTPUT_FILE = Path("Scripts/deliverable.md")

MODULES = [
    "autores", "clientes", "estoque", "fotos", "funcionarios",
    "generos", "historico", "livros", "logs", "movimentacao",
    "multas", "reservas", "auth"
]

ENTITY_DETAILS = {
    "Autor": {
        "table": "Autores",
        "attributes": [
            "Integer idAutor",
            "String nomeAutor",
        ],
        "methods": ["getIdAutor() Integer", "setIdAutor(Integer)", "getNomeAutor() String", "setNomeAutor(String)"],
    },
    "Cliente": {
        "table": "Clientes",
        "attributes": [
            "Integer idCliente",
            "String nomeCliente",
            "String senhaCliente",
            "String cpf",
            "LocalDate dataNascimento",
            "String telefone",
            "String email",
            "String endereco",
            "Boolean bloqueado",
            "Integer tentativasLogin",
            "Boolean emailConfirmado",
        ],
        "methods": [],
    },
    "Estoque": {
        "table": "Estoque",
        "attributes": [
            "Integer idEstoque",
            "Integer idLivro",
            "Integer quantidadeTotal",
            "Integer quantidadeReservada",
            "Integer quantidadeEmprestada",
            "Integer quantidadeDanificada",
            "Integer estoqueMinimo",
        ],
        "methods": [],
    },
    "Foto": {
        "table": "Fotos",
        "attributes": [
            "Integer idFoto",
            "Integer idLivro",
            "String foto",
        ],
        "methods": [],
    },
    "Funcionario": {
        "table": "Funcionarios",
        "attributes": [
            "Integer idFuncionario",
            "String nome",
            "String email",
            "String senha",
            "Character perfil",
        ],
        "methods": [],
    },
    "Genero": {
        "table": "Generos",
        "attributes": [
            "Integer idGenero",
            "String nomeGenero",
        ],
        "methods": ["getIdGenero() Integer", "setIdGenero(Integer)", "getNomeGenero() String", "setNomeGenero(String)"],
    },
    "HistoricoCliente": {
        "table": "Historico_cliente",
        "attributes": [
            "Integer idHistorico",
            "Integer idCliente",
            "String campoAlterado",
            "String valorAntigo",
            "String valorNovo",
            "LocalDate dataAlteracao",
        ],
        "methods": [],
    },
    "Livro": {
        "table": "Livros",
        "attributes": [
            "Integer idLivro",
            "String nomeLivro",
            "String isnb",
            "String editora",
            "Integer ano",
            "String sinopse",
            "String localizacaoFisica",
        ],
        "methods": [],
    },
    "Log": {
        "table": "Logs",
        "attributes": [
            "Integer idLog",
            "Integer idCliente",
            "Integer idFuncionario",
            "String acao",
            "LocalDateTime dataAcao",
            "String ip",
        ],
        "methods": [],
    },
    "MovimentacaoEstoque": {
        "table": "Movimentacao_estoque",
        "attributes": [
            "Integer idMovimentacao",
            "Integer idLivro",
            "Character tipoMovimentacao",
            "Integer quantidade",
            "LocalDate dataMovimentacao",
            "String motivo",
        ],
        "methods": [],
    },
    "Multa": {
        "table": "Multas",
        "attributes": [
            "Integer idMulta",
            "Integer idReserva",
            "BigDecimal valorMulta",
            "LocalDate dataMulta",
            "String statusMulta",
            "LocalDate dataPagamento",
        ],
        "methods": [],
    },
    "Reserva": {
        "table": "Reservas",
        "attributes": [
            "Integer idReserva",
            "Integer idCliente",
            "Integer idLivro",
            "Integer idFuncionarioRetirada",
            "Integer idFuncionarioDevolucao",
            "LocalDate dataReserva",
            "LocalDate dataLimiteRetirada",
            "LocalDate dataRetirada",
            "LocalDate dataPrevistaDevolucao",
            "LocalDate dataDevolucao",
            "String statusReserva",
            "String codigoReserva",
        ],
        "methods": [],
    },
}


def find_java_files():
    """Find all .java files in the backend source directory."""
    java_files = []
    for root, dirs, files in os.walk(BACKEND_SRC):
        for f in files:
            if f.endswith(".java"):
                full_path = Path(root) / f
                rel_path = full_path.relative_to(BACKEND_SRC.parent.parent.parent)
                java_files.append((rel_path, full_path))
    return sorted(java_files, key=lambda x: str(x[0]))


def classify_file(rel_path_str):
    """Classify a Java file by its role."""
    if "/data/" in rel_path_str:
        return "Entity"
    elif "/repository/" in rel_path_str:
        return "Repository"
    elif "/controller/" in rel_path_str:
        return "Controller"
    elif "/models/" in rel_path_str:
        return "DTO"
    elif "/services/" in rel_path_str:
        return "Service"
    else:
        return "Other"


def get_module_name(rel_path_str):
    """Extract module name from path."""
    parts = rel_path_str.split("/")
    for p in parts:
        if p in MODULES:
            return p
    return "auth"


def read_file_content(path):
    """Read file content as string."""
    with open(path, "r", encoding="utf-8") as f:
        return f.read()


def strip_imports(content):
    """Remove import lines from Java source code."""
    return "\n".join(
        line for line in content.split("\n")
        if not line.strip().startswith("import ")
    )


def generate_mermaid_class_diagram():
    """Generate Mermaid class diagram for entities."""
    lines = []
    lines.append("```mermaid")
    lines.append("classDiagram")
    lines.append("")

    for entity_name, details in ENTITY_DETAILS.items():
        lines.append(f"    note for {entity_name} \"Tabela: {details['table']}\"")
        lines.append(f"    class {entity_name} {{")
        for attr in details["attributes"]:
            lines.append(f"        +{attr}")
        for m in details["methods"]:
            lines.append(f"        +{m}")
        lines.append(f"    }}")
        lines.append("")

    lines.append("    Livro \"*\" --> \"*\" Autor : @ManyToMany")
    lines.append("    Livro \"*\" --> \"*\" Genero : @ManyToMany")
    lines.append("    Livro \"1\" --> \"1\" Estoque : @OneToOne")
    lines.append("    Livro \"1\" --> \"*\" Foto : @OneToMany")
    lines.append("    Livro \"1\" --> \"*\" MovimentacaoEstoque : @OneToMany")
    lines.append("    Livro \"1\" --> \"*\" Reserva : @OneToMany")
    lines.append("    Cliente \"1\" --> \"*\" HistoricoCliente : @OneToMany")
    lines.append("    Cliente \"1\" --> \"*\" Reserva : @OneToMany")
    lines.append("    Reserva \"1\" --> \"*\" Multa : @OneToMany")
    lines.append("    Reserva \"*\" --> \"1\" Funcionario : retirada")
    lines.append("    Reserva \"*\" --> \"1\" Funcionario : devolucao")
    lines.append("    Cliente \"1\" --> \"*\" Log : @OneToMany")
    lines.append("    Funcionario \"1\" --> \"*\" Log : @OneToMany")

    lines.append("")
    lines.append("```")
    return "\n".join(lines)


def generate_source_section(java_files):
    """Generate source code section organized by module and type."""
    sections = []

    # Group by module
    module_files = {}
    for rel_path, full_path in java_files:
        module = get_module_name(str(rel_path))
        if module not in module_files:
            module_files[module] = []
        module_files[module].append((rel_path, full_path))

    for module in MODULES:
        if module not in module_files:
            continue
        files = module_files[module]

        module_name_map = {
            "autores": "Autores",
            "clientes": "Clientes",
            "estoque": "Estoque",
            "fotos": "Fotos",
            "funcionarios": "Funcionarios",
            "generos": "Generos",
            "historico": "HistoricoCliente",
            "livros": "Livros",
            "logs": "Logs",
            "movimentacao": "MovimentacaoEstoque",
            "multas": "Multas",
            "reservas": "Reservas",
            "auth": "Autenticacao",
        }

        sections.append(f"## Módulo: {module_name_map.get(module, module)}\n")

        # Order: Entity, DTO, Repository, Controller, Service, Other
        order = {"Entity": 0, "DTO": 1, "Repository": 2, "Controller": 3, "Service": 4, "Other": 5}
        files.sort(key=lambda x: (order.get(classify_file(str(x[0])), 99), str(x[0])))

        for rel_path, full_path in files:
            classification = classify_file(str(rel_path))
            content = strip_imports(read_file_content(full_path))
            file_name = rel_path.name

            sections.append(f"### {file_name} ({classification})\n")
            sections.append(f"**Caminho:** `{rel_path}`\n")
            sections.append("```java")
            sections.append(content)
            sections.append("```\n")

    return "\n".join(sections)


def generate_deliverable():
    """Generate the complete deliverable document."""
    java_files = find_java_files()

    doc = []
    doc.append("# Biblioteca - Projeto de Programa\u00e7\u00e3o Orientada a Objetos\n")
    doc.append("")
    doc.append("## Sum\u00e1rio\n")
    doc.append("")
    doc.append("- [Descri\u00e7\u00e3o do Projeto](#descri\u00e7\u00e3o-do-projeto)")
    doc.append("- [Membros da Equipe](#membros-da-equipe)")
    doc.append("- [Tecnologias do Back-End](#tecnologias-do-back-end)")
    doc.append("- [Diagrama de Classes](#diagrama-de-classes)")
    doc.append("    - [Legenda dos Relacionamentos](#legenda-dos-relacionamentos)")
    doc.append("- [C\u00f3digo Fonte](#c\u00f3digo-fonte)")
    doc.append("    - [Autores](#m\u00f3dulo-autores)")
    doc.append("    - [Clientes](#m\u00f3dulo-clientes)")
    doc.append("    - [Estoque](#m\u00f3dulo-estoque)")
    doc.append("    - [Fotos](#m\u00f3dulo-fotos)")
    doc.append("    - [Funcion\u00e1rios](#m\u00f3dulo-funcionarios)")
    doc.append("    - [G\u00eaneros](#m\u00f3dulo-generos)")
    doc.append("    - [Hist\u00f3rico do Cliente](#m\u00f3dulo-historicocliente)")
    doc.append("    - [Livros](#m\u00f3dulo-livros)")
    doc.append("    - [Logs](#m\u00f3dulo-logs)")
    doc.append("    - [Movimenta\u00e7\u00e3o de Estoque](#m\u00f3dulo-movimentacaoestoque)")
    doc.append("    - [Multas](#m\u00f3dulo-multas)")
    doc.append("    - [Reservas](#m\u00f3dulo-reservas)")
    doc.append("    - [Autentica\u00e7\u00e3o](#m\u00f3dulo-autenticacao)")
    doc.append("- [Refer\u00eancias](#refer\u00eancias)")
    doc.append("")
    doc.append("---\n")
    doc.append("")
    doc.append("### Descri\u00e7\u00e3o do Projeto\n")
    doc.append("")
    doc.append("A Biblioteca \u00e9 um sistema para gerenciar bibliotecas f\u00edsicas, permitindo o cadastro de livros, autores, g\u00eaneros e clientes, al\u00e9m de controlar o estoque, empr\u00e9stimos, reservas e multas. O sistema conta com dois perfis de acesso: clientes (que podem consultar o acervo e realizar reservas) e funcion\u00e1rios (que gerenciam as opera\u00e7\u00f5es da biblioteca). Foi desenvolvido como uma API REST utilizando Java e o framework Quarkus, com banco de dados PostgreSQL.\n")
    doc.append("")
    doc.append("### Membros da Equipe\n")
    doc.append("")
    doc.append("| RA | Nome |")
    doc.append("|----|------|")
    doc.append("| 20240864 | Bruno Pereira de Souza |")
    doc.append("| 20240770 | Fernando Petri |")
    doc.append("| 20240012 | Jhonatan Rosendo da Silva Alves |")
    doc.append("| 20240798 | Jonathan Martins Melgar |")
    doc.append("| 20240807 | Michelli Segantini da Silva de Lima |")
    doc.append("| 20240289 | Vinicius Eduardo da Silva |")
    doc.append("")
    doc.append("### Tecnologias do Back-End\n")
    doc.append("")
    doc.append("| Tecnologia | Descri\u00e7\u00e3o |")
    doc.append("|------------|-----------|")
    doc.append("| **Java 21** | Linguagem de programa\u00e7\u00e3o |")
    doc.append("| **Quarkus 3.32** | Framework REST |")
    doc.append("| **Hibernate ORM com Panache** | Mapeamento objeto-relacional (JPA) |")
    doc.append("| **PostgreSQL** | Banco de dados relacional |")
    doc.append("| **Flyway** | Migra\u00e7\u00e3o de banco de dados |")
    doc.append("| **Maven** | Gerenciamento de depend\u00eancias e build |")
    doc.append("| **SmallRye OpenAPI** | Documenta\u00e7\u00e3o da API (Swagger UI) |")
    doc.append("| **Hibernate Validator** | Valida\u00e7\u00e3o de dados |")
    doc.append("")
    doc.append("---\n")
    doc.append("")

    # ============================================================
    # Section 1: Class Diagram (Mermaid)
    # ============================================================
    doc.append("## Diagrama de Classes\n")
    doc.append("")
    doc.append("Diagrama focado nas entidades JPA e seus relacionamentos no banco de dados.\n")
    doc.append("")
    doc.append(generate_mermaid_class_diagram())
    doc.append("")

    # Add relationship summary
    doc.append("### Legenda dos Relacionamentos\n")
    doc.append("")
    doc.append("| Entidade A | Relacionamento | Entidade B | Multiplicidade | Descri\u00e7\u00e3o |")
    doc.append("|------------|---------------|------------|----------------|------|")
    doc.append("| Livro | * -- * | Autor | N..N | @ManyToMany via tabela Livro_Autor |")
    doc.append("| Livro | * -- * | Genero | N..N | @ManyToMany via tabela Genero_livro |")
    doc.append("| Livro | 1 -- 1 | Estoque | 1..1 | @OneToOne - invent\u00e1rio do livro |")
    doc.append("| Livro | 1 -- * | Foto | 1..N | @OneToMany - fotos do livro |")
    doc.append("| Livro | 1 -- * | MovimentacaoEstoque | 1..N | @OneToMany - mov. de estoque |")
    doc.append("| Livro | 1 -- * | Reserva | 1..N | @OneToMany - reservas do livro |")
    doc.append("| Cliente | 1 -- * | HistoricoCliente | 1..N | @OneToMany - hist\u00f3rico do cliente |")
    doc.append("| Cliente | 1 -- * | Reserva | 1..N | @OneToMany - reservas do cliente |")
    doc.append("| Reserva | 1 -- * | Multa | 1..N | @OneToMany - multas da reserva |")
    doc.append("| Reserva | * -- 1 | Funcionario | N..1 | @ManyToOne - funcion\u00e1rio retirada |")
    doc.append("| Reserva | * -- 1 | Funcionario | N..1 | @ManyToOne - funcion\u00e1rio devolu\u00e7\u00e3o |")
    doc.append("| Cliente | 1 -- * | Log | 1..N | @OneToMany - logs do cliente |")
    doc.append("| Funcionario | 1 -- * | Log | 1..N | @OneToMany - logs do funcion\u00e1rio |")
    doc.append("")

    # ============================================================
    # Section 2: Source Code
    # ============================================================
    doc.append("# C\u00f3digo Fonte\n")
    doc.append("")
    doc.append("O projeto \u00e9 organizado em 13 m\u00f3dulos, totalizando 54 classes com as seguintes responsabilidades:\n")
    doc.append("")
    doc.append("| Tipo | Quantidade | Descri\u00e7\u00e3o |")
    doc.append("|------|------------|-------------|")
    doc.append("| **Entity** | 12 | Modelos JPA que mapeiam as tabelas do banco de dados |")
    doc.append("| **Repository** | 12 | Camada de acesso a dados (PanacheRepository) |")
    doc.append("| **Controller** | 13 | Endpoints REST que recebem e respondem requisi\u00e7\u00f5es HTTP |")
    doc.append("| **DTO** | 14 | Objetos de transfer\u00eancia entre a API e as entidades |")
    doc.append("| **Service** | 2 | L\u00f3gica de neg\u00f3cio (TokenService, LogService) |")
    doc.append("| **Other** | 1 | Exce\u00e7\u00e3o personalizada (AuthException) |")
    doc.append("")
    doc.append("Cada m\u00f3dulo segue uma arquitetura em camadas: `Entity` \u2192 `Repository` \u2192 `Controller`, com `DTO` para transporte de dados.\n")
    doc.append("")
    doc.append(generate_source_section(java_files))

    # ============================================================
    # Section 3: Refer\u00eancias
    # ============================================================
    doc.append("## Refer\u00eancias\n")
    doc.append("")
    doc.append("- **Reposit\u00f3rio GitHub:** [https://github.com/anomalyco/Biblioteca](https://github.com/anomalyco/Biblioteca)")
    doc.append("")

    # Write output
    content = "\n".join(doc)
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"Deliverable generated: {OUTPUT_FILE}")
    print(f"Total Java files included: {len(java_files)}")
    print(f"Total size: {len(content)} characters")


if __name__ == "__main__":
    generate_deliverable()
