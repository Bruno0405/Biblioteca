CREATE TABLE IF NOT EXISTS Funcionarios (
                                            id_funcionario INT GENERATED ALWAYS AS IDENTITY,
                                            nome dm_padrao NOT NULL,
                                            email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(250) NOT NULL,
    perfil CHAR(1) NOT NULL,

    PRIMARY KEY (id_funcionario)
    );

CREATE TABLE IF NOT EXISTS Clientes (
                                        id_cliente INT GENERATED ALWAYS AS IDENTITY,
                                        nome_cliente dm_padrao NOT NULL,
                                        senha_cliente dm_padrao NOT NULL,
                                        cpf CHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    email dm_padrao UNIQUE NOT NULL,
    endereco dm_padrao NOT NULL,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    tentativas_login INT NOT NULL DEFAULT 0,
    email_confirmado BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id_cliente)
    );

CREATE TABLE IF NOT EXISTS Generos (
                                       id_genero INT GENERATED ALWAYS AS IDENTITY,
                                       nome_genero dm_padrao NOT NULL,

                                       PRIMARY KEY (id_genero)
    );

CREATE TABLE IF NOT EXISTS Genero_livro (
                                            id_genero INT NOT NULL,
                                            id_livro INT NOT NULL,

                                            PRIMARY KEY (id_genero, id_livro),
    FOREIGN KEY (id_genero) REFERENCES Generos(id_genero),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
    );

CREATE TABLE IF NOT EXISTS Autores (
                                       id_autor INT GENERATED ALWAYS AS IDENTITY,
                                       nome_autor dm_padrao NOT NULL,

                                       PRIMARY KEY (id_autor)
    );

CREATE TABLE IF NOT EXISTS Livro_Autor (
                                           id_livro INT NOT NULL,
                                           id_autor INT NOT NULL,

                                           PRIMARY KEY (id_livro, id_autor),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro),
    FOREIGN KEY (id_autor) REFERENCES Autores(id_autor)
    );

CREATE TABLE IF NOT EXISTS Fotos (
                                     id_foto INT GENERATED ALWAYS AS IDENTITY,
                                     id_livro INT NOT NULL,
                                     foto TEXT NOT NULL,

                                     PRIMARY KEY (id_foto),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
    );

CREATE TABLE IF NOT EXISTS Historico_cliente (
                                                 id_historico INT GENERATED ALWAYS AS IDENTITY,
                                                 id_cliente INT NOT NULL,
                                                 campo_alterado dm_padrao NOT NULL,
                                                 valor_antigo TEXT,
                                                 valor_novo TEXT,
                                                 data_alteracao DATE NOT NULL,

                                                 PRIMARY KEY (id_historico),
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente)
    );

CREATE TABLE IF NOT EXISTS Estoque (
                                       id_estoque INT GENERATED ALWAYS AS IDENTITY,
                                       id_livro INT UNIQUE NOT NULL,
                                       quantidade_total INT NOT NULL DEFAULT 0,
                                       quantidade_reservada INT NOT NULL DEFAULT 0,
                                       quantidade_emprestada INT NOT NULL DEFAULT 0,
                                       quantidade_danificada INT NOT NULL DEFAULT 0,
                                       estoque_minimo INT NOT NULL,

                                       PRIMARY KEY (id_estoque),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
    );

CREATE TABLE IF NOT EXISTS Movimentacao_estoque (
                                                    id_movimentacao INT GENERATED ALWAYS AS IDENTITY,
                                                    id_livro INT NOT NULL,
                                                    tipo_movimentacao CHAR(1) NOT NULL,
    quantidade INT NOT NULL,
    data_movimentacao DATE NOT NULL,
    motivo TEXT NOT NULL,

    CHECK (tipo_movimentacao IN ('E','P','A','X')),
    PRIMARY KEY (id_movimentacao),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
    );

CREATE TABLE IF NOT EXISTS Reservas (
                                        id_reserva INT GENERATED ALWAYS AS IDENTITY,
                                        id_cliente INT NOT NULL,
                                        id_livro INT NOT NULL,
                                        id_funcionario_retirada INT,
                                        id_funcionario_devolucao INT,
                                        data_reserva DATE NOT NULL,
                                        data_limite_retirada DATE,
                                        data_retirada DATE,
                                        data_prevista_devolucao DATE,
                                        data_devolucao DATE,
                                        status_reserva VARCHAR(15) NOT NULL DEFAULT 'reservado',
    codigo_reserva INT,

    PRIMARY KEY (id_reserva),
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro),
    FOREIGN KEY (id_funcionario_retirada) REFERENCES Funcionarios(id_funcionario),
    FOREIGN KEY (id_funcionario_devolucao) REFERENCES Funcionarios(id_funcionario),

    CHECK (status_reserva IN ('reservado','emprestado','devolvido','cancelado','atrasado'))
    );

CREATE TABLE IF NOT EXISTS Multas (
                                      id_multa INT GENERATED ALWAYS AS IDENTITY,
                                      id_reserva INT NOT NULL UNIQUE,
                                      valor_multa DECIMAL(5,2) NOT NULL,
    data_multa DATE NOT NULL,
    status_multa CHAR(8) NOT NULL,
    data_pagamento DATE,

    PRIMARY KEY (id_multa),
    FOREIGN KEY (id_reserva) REFERENCES Reservas(id_reserva)
    );

CREATE TABLE IF NOT EXISTS Logs (
                                    id_log INT GENERATED ALWAYS AS IDENTITY,
                                    id_cliente INT,
                                    id_funcionario INT,
                                    acao TEXT NOT NULL,
                                    data_acao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    ip INET,

                                    PRIMARY KEY (id_log),
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
    FOREIGN KEY (id_funcionario) REFERENCES Funcionarios(id_funcionario)
    );

CREATE INDEX IF NOT EXISTS idx_autores_id_autor ON Autores(id_autor);
CREATE INDEX IF NOT EXISTS idx_generos_id_genero ON Generos(id_genero);