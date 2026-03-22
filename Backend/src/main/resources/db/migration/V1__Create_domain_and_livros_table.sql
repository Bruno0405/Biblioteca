CREATE DOMAIN dm_padrao VARCHAR (80);

CREATE TABLE Livros (
    id_livro INT GENERATED ALWAYS AS IDENTITY,
    nome_livro dm_padrao NOT NULL,
    isnb CHAR(13) UNIQUE,
    editora dm_padrao NOT NULL,
    ano INT,
    sinopse TEXT,
    localizacao_fisica dm_padrao,
    data_adicao DATE NOT NULL,

    PRIMARY KEY (id_livro)
);