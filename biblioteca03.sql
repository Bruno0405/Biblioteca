--CREATE DOMAIN dm_padrao VARCHAR (80);

--VERSÃO 3


-- Tudo depois do senha_cliente é novo

CREATE TABLE Clientes (
	id_cliente INT NOT NULL,
	nome_cliente dm_padrao NOT NULL,
	senha_cliente dm_padrao NOT NULL,
	cpf CHAR(11) UNIQUE NOT NULL,
	data_nascimento DATE NOT NULL,
	telefone VARCHAR(15) NOT NULL,
	email dm_padrao UNIQUE NOT NULL,
	endereco dm_padrao NOT NULL,
	bloqueado BOOLEAN NOT NULL DEFAULT FALSE, 
	tentativas_login INT NOT NULL DEFAULT,
	email_confirmado BOOLEAN NOT NULL DEFAULT 0 FALSE,

	PRIMARY KEY (id_cliente)
	
);
-----------------------------------------------------------------------------------------------------------------------
--Tudo de isbn para baixo e novo

CREATE TABLE Livros (
	id_livro INT NOT NULL,
	nome_livro dm_padrao NOT NULL,
	isnb CHAR(13) UNIQUE,
 	editora dm_padrao NOT NULL,
	ano INT,
 	sinopse TEXT,
 	localizacao_fisica dm_padrao,

	PRIMARY KEY (id_livro)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Generos (
	id_genero INT NOT NULL,
	nome_genero dm_padrao NOT NULL,

	PRIMARY KEY (id_genero)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Genero_livro (
	id_genero INT NOT NULL, 
	id_livro INT NOT NULL,

	PRIMARY KEY (id_genero, id_livro),
	FOREIGN KEY (id_genero) REFERENCES Generos(id_genero),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Autores (
	id_autor INT NOT NULL,
	nome_autor dm_padrao NOT NULL,
	
	PRIMARY KEY (id_autor)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Livro_Autor(
	id_livro INT NOT NULL,
	id_autor INT NOT NULL,
	PRIMARY KEY (id_livro,id_autor),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro),
	FOREIGN KEY (id_autor) REFERENCES Autores(id_autor)
	
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Fotos (
	id_foto INT NOT NULL,
	id_livro INT NOT NULL,
	foto TEXT NOT NULL,

	PRIMARY KEY (id_foto),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
);
-------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Reservas (
	id_reserva INT NOT NULL,
	id_cliente INT NOT NULL,
	id_livro INT NOT NULL,
	id_funcionario_retirada INT,
	id_funcionario_devolucao INT,
	data_reserva DATE NOT NULL,
	data_limite_retirada DATE,
	data_retirada DATE,
	data_prevista_devolucao DATE,
	data_devolucao DATE,
	status_reserva CHAR(15) NOT NULL DEFAULT 'reservado',
	codigo_reserva INT,

	PRIMARY KEY (id_reserva),
	FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro),
	FOREIGN KEY (id_funcionario_retirada) REFERENCES Funcionarios(id_funcionario),
	FOREIGN KEY (id_funcionario_devolucao) REFERENCES Funcionarios(id_funcionario),

	CHECK (status_reserva IN ('reservado','emprestado','devolvido','cancelado','atrasado'))
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Multas (
	id_multa INT NOT NULL,
	id_reserva INT NOT NULL UNIQUE ,
	valor_multa DECIMAL(5,2) NOT NULL,
	data_multa DATE NOT NULL,
	status_multa CHAR(8) NOT NULL,
	data_pagamento DATE,

	PRIMARY KEY (id_multa),
	FOREIGN KEY (id_reserva) REFERENCES Reservas(id_reserva)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Historico_cliente (
	id_historico INT NOT NULL,
	id_cliente INT NOT NULL,
	campo_alterado VARCHAR NOT NULL,
	valor_antigo TEXT,
	valor_novo TEXT,
	data_alteracao DATE NOT NULL,

	PRIMARY KEY (id_historico),
	FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Estoque (
	id_estoque INT NOT NULL,
	id_livro INT UNIQUE  NOT NULL,
	quantidade_total INT NOT NULL DEFAULT 0,
	quantidade_reservada INT NOT NULL DEFAULT 0,
	quantidade_emprestada INT NOT NULL DEFAULT 0,
	quantidade_danificada INT NOT NULL DEFAULT 0,
	estoque_minimo INT NOT NULL,

	PRIMARY KEY (id_estoque),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
);
---------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Movimentacao_estoque (
	id_movimentacao INT NOT NULL,
	id_livro INT NOT NULL,
	tipo_movimentacao CHAR (1) NOT NULL, 
	quantidade INT NOT NULL,
	data_movimentacao DATE NOT NULL,
	motivo TEXT NOT NULL,
	
	CHECK (tipo_movimentacao IN('E','P','A','X')),
	PRIMARY KEY (id_movimentacao),
	FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
);
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Funcionarios (
	id_funcionario INT NOT NULL,
	nome dm_padrao NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	senha VARCHAR(250) NOT NULL,
	perfil  CHAR (1) NOT NULL,

	PRIMARY KEY (id_funcionario)

);
--------------------------------------------------------------------------------------------------------------------------

CREATE TABLE Logs(	
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
--------------------------------------------------------------------------------------------------------------------------



--EXPLICAÇÕES PARA OS COLEGUINHAS 

--perfil CHAR (1) NOT NULL,
-- A = Administrador, G = Gerente, F = Funcionario

--tipo_movimentacao CHAR (1) 
--E = entrada
--P = perda
--A = ajuste
--X = danificado






















