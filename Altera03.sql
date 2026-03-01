--Arquivo de alteração para o biblioteca03

ALTER TABLE Clientes
ADD COLUMN cpf CHAR(11)UNIQUE NOT NULL,
ADD COLUMN data_nascimento DATE NOT NULL,
ADD COLUMN telefone VARCHAR(15) NOT NULL,
ADD COLUMN email dm_padrao UNIQUE NOT NULL,
ADD COLUMN endereco dm_padrao NOT NULL,
ADD COLUMN bloqueado BOOLEAN NOT NULL DEFAULT FALSE, 
ADD COLUMN tentativas_login INT,
ADD COLUMN email_confirmado BOOLEAN NOT NULL DEFAULT FALSE;

--------------------------------------------------------------------------------------------------------------------------
ALTER TABLE Livros
ADD COLUMN editora dm_padrao NOT NULL,
ADD COLUMN ano INT,
ADD COLUMN sinopse TEXT,
ADD COLUMN localizacao_fisica dm_padrao,
ADD COLUMN data_adicao DATE NOT NULL;

--------------------------------------------------------------------------------------------------------------------------

--Tabela totalmente nova

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

-------------------------------------------------------------------------------------------------------------------------

--Tabela totalment nova 

CREATE TABLE Funcionarios (
	id_funcionario INT NOT NULL,
	nome dm_padrao NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	senha VARCHAR(250) NOT NULL,
	perfil  CHAR (1) NOT NULL,

	PRIMARY KEY (id_funcionario)

);

---------------------------------------------------------------------------------------------------------------------------

--Alterando coisas no Historico_cliente

ALTER TABLE Historico_cliente
ALTER COLUMN valor_antigo TYPE TEXT,
ALTER COLUMN valor_novo TYPE TEXT,
ALTER COLUMN campo_alterado TYPE dm_padrao,
ADD CONSTRAINT fk_historico_cliente
FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente);

---------------------------------------------------------------------------------------------------------------------------

--Tabela totalmente nova

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

ALTER TABLE Reservas
ADD COLUMN id_funcionario_retirada INT,
ADD COLUMN id_funcionario_devolucao INT,
ADD COLUMN data_limite_retirada DATE,
ADD COLUMN data_retirada DATE,
ADD COLUMN data_prevista_devolucao DATE,
ADD COLUMN data_devolucao DATE,
ADD COLUMN codigo_reserva INT;

ALTER TABLE Reservas
ADD CONSTRAINT fk_reserva_func_retirada
FOREIGN KEY (id_funcionario_retirada)
REFERENCES Funcionarios(id_funcionario);

ALTER TABLE Reservas
ADD CONSTRAINT fk_reserva_func_devolucao
FOREIGN KEY (id_funcionario_devolucao)
REFERENCES Funcionarios(id_funcionario);

ALTER TABLE Reservas
ADD CONSTRAINT chk_status_reserva
CHECK (status_reserva IN ('reservado','emprestado','devolvido','cancelado','atrasado'));

ALTER TABLE Reservas
ALTER COLUMN status_reserva SET DEFAULT 'reservado';
------------------------------------------------------------------------------------------------------------------------

ALTER TABLE Multas
ADD COLUMN data_pagamento DATE;
------------------------------------------------------------------------------------------------------------------------

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






























































