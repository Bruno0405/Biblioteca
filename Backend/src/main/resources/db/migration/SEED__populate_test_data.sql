-- Repeatable migration: seed test data for development and testing.
-- Runs after all versioned migrations on every startup where the checksum changed.
-- Uses INSERT ... WHERE NOT EXISTS so it is safe to re-run.
-- OVERRIDING SYSTEM VALUE bypasses GENERATED ALWAYS AS IDENTITY on PK columns.
-- All passwords are BCrypt hash of '123' (cost 10).

-- ============================================================
-- 1. FUNCIONARIOS
-- ============================================================
INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil) OVERRIDING SYSTEM VALUE
SELECT 1, 'Administrador Teste',   'admin@biblioteca.com',       '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', 'A'
WHERE NOT EXISTS (SELECT 1 FROM Funcionarios WHERE email = 'admin@biblioteca.com');

INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil) OVERRIDING SYSTEM VALUE
SELECT 2, 'Atendente Teste',       'atendente@biblioteca.com',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', 'F'
WHERE NOT EXISTS (SELECT 1 FROM Funcionarios WHERE email = 'atendente@biblioteca.com');

INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil) OVERRIDING SYSTEM VALUE
SELECT 3, 'Gerente Teste',         'gerente@biblioteca.com',     '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', 'G'
WHERE NOT EXISTS (SELECT 1 FROM Funcionarios WHERE email = 'gerente@biblioteca.com');

INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil) OVERRIDING SYSTEM VALUE
SELECT 4, 'Carlos Silva',          'carlos@biblioteca.com',      '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', 'F'
WHERE NOT EXISTS (SELECT 1 FROM Funcionarios WHERE email = 'carlos@biblioteca.com');

INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil) OVERRIDING SYSTEM VALUE
SELECT 5, 'Ana Oliveira',          'ana@biblioteca.com',         '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', 'A'
WHERE NOT EXISTS (SELECT 1 FROM Funcionarios WHERE email = 'ana@biblioteca.com');

-- ============================================================
-- 2. CLIENTES
-- ============================================================
INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 1,  'Cliente Teste',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '12345678901', '1995-05-10', '11999999999', 'cliente@biblioteca.com', 'Rua das Letras, 100',             FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'cliente@biblioteca.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 2,  'Maria Leitora',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '98765432100', '2000-08-21', '11888888888', 'maria@biblioteca.com',  'Avenida Central, 250',            FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'maria@biblioteca.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 3,  'João Pereira',    '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '11122233300', '1988-03-15', '21977777777', 'joao@email.com',        'Rua dos Pinheiros, 50',           FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'joao@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 4,  'Carla Souza',     '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '22233344400', '1992-11-08', '31966666666', 'carla@email.com',       'Av. Brasil, 300',                 FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'carla@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 5,  'Pedro Almeida',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '33344455500', '1985-07-22', '41955555555', 'pedro@email.com',       'Rua das Flores, 120',             FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'pedro@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 6,  'Lucia Fernandes', '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '44455566600', '1998-01-30', '51944444444', 'lucia@email.com',       'Rua do Sol, 75',                  FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'lucia@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 7,  'Roberto Lima',    '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '55566677700', '1990-09-12', '61933333333', 'roberto@email.com',     'Av. Paulista, 1000',              FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'roberto@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 8,  'Juliana Costa',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '66677788800', '2002-04-05', '71922222222', 'juliana@email.com',     'Rua das Palmeiras, 40',           FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'juliana@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 9,  'Marcos Santos',   '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '77788899900', '1980-12-25', '81911111111', 'marcos@email.com',      'Rua Augusta, 500',                FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'marcos@email.com');

INSERT INTO Clientes (id_cliente, nome_cliente, senha_cliente, cpf, data_nascimento, telefone, email, endereco, bloqueado, tentativas_login, email_confirmado) OVERRIDING SYSTEM VALUE
SELECT 10, 'Patricia Gomes',  '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW', '88899900000', '1996-06-18', '11988887777', 'patricia@email.com',   'Rua do Comércio, 200',            FALSE, 0, TRUE
WHERE NOT EXISTS (SELECT 1 FROM Clientes WHERE email = 'patricia@email.com');

-- ============================================================
-- 3. AUTORES
-- ============================================================
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 1,  'Machado de Assis'          WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 1);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 2,  'Clarice Lispector'         WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 2);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 3,  'Jorge Amado'               WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 3);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 4,  'George Orwell'             WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 4);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 5,  'J. K. Rowling'             WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 5);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 6,  'Gabriel García Márquez'    WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 6);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 7,  'Guimarães Rosa'            WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 7);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 8,  'Agatha Christie'           WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 8);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 9,  'Carlos Drummond de Andrade' WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 9);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 10, 'Stephen King'              WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 10);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 11, 'Antoine de Saint-Exupéry'  WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 11);
INSERT INTO Autores (id_autor, nome_autor) OVERRIDING SYSTEM VALUE
SELECT 12, 'Anne Frank'                WHERE NOT EXISTS (SELECT 1 FROM Autores WHERE id_autor = 12);

-- ============================================================
-- 4. GENEROS
-- ============================================================
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 1, 'Ficção'     WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 1);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 2, 'Romance'    WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 2);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 3, 'Fantasia'   WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 3);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 4, 'Terror'     WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 4);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 5, 'Não-ficção' WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 5);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 6, 'Poesia'     WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 6);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 7, 'Biografia'  WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 7);
INSERT INTO Generos (id_genero, nome_genero) OVERRIDING SYSTEM VALUE
SELECT 8, 'Mistério'   WHERE NOT EXISTS (SELECT 1 FROM Generos WHERE id_genero = 8);

-- ============================================================
-- 5. LIVROS
-- ============================================================
INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 1,  'Dom Casmurro',          '9788535902772', 'Companhia das Letras',   1899, 'Bentinho e Capitu vivem uma história de amor e ciúmes no Rio de Janeiro do século XIX.', 'P1-E1'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 1);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 2,  'A Hora da Estrela',     '9788532505100', 'Rocco',                  1977, 'Macabéa, uma nordestina pobre e ignorante, busca seu lugar no Rio de Janeiro.', 'P1-E2'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 2);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 3,  'Capitães da Areia',     '9788535903908', 'Companhia das Letras',   1937, 'Meninos de rua em Salvador lutam pela sobrevivência formando o grupo Capitães da Areia.', 'P1-E3'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 3);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 4,  '1984',                  '9788535914843', 'Companhia das Letras',   1949, 'Winston Smith vive sob o regime totalitário do Grande Irmão em uma sociedade distópica.', 'P2-E1'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 4);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 5,  'Harry Potter e a Pedra Filosofal', '9788532518063', 'Rocco', 1997, 'Harry descobre que é um bruxo e ingressa na Escola de Magia e Bruxaria de Hogwarts.', 'P2-E2'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 5);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 6,  'Cem Anos de Solidão',   '9788535909559', 'Companhia das Letras',   1967, 'A história da família Buendía na cidade fictícia de Macondo.', 'P2-E3'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 6);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 7,  'Grande Sertão: Veredas', '9788535900969', 'Companhia das Letras',  1956, 'Riobaldo narra suas aventuras no sertão brasileiro e seu amor por Diadorim.', 'P3-E1'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 7);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 8,  'O Assassinato no Expresso do Oriente', '9788501085186', 'Record', 1934, 'Hercule Poirot investiga um assassinato a bordo do lendário Expresso do Oriente.', 'P3-E2'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 8);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 9,  'It: A Coisa',           '9788560106023', 'Suma',                   1986, 'Sete amigos enfrentam uma entidade maligna que assume a forma de um palhaço.', 'P3-E3'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 9);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 10, 'A Revolução dos Bichos', '9788535913402', 'Companhia das Letras',  1945, 'Animais de uma fazenda se rebelam contra seus donos humanos.', 'P4-E1'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 10);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 11, 'Alguma Poesia',         '9788535931840', 'Companhia das Letras',   1930, 'Primeiro livro de poemas de Drummond, marcado pelo modernismo brasileiro.', 'P4-E2'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 11);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 12, 'O Pequeno Príncipe',    '9788595085761', 'HarperCollins Brasil',   1943, 'Um piloto perdido no deserto encontra um pequeno príncipe vindo de outro planeta.', 'P4-E3'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 12);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 13, 'Coraline',              '9788532510593', 'Rocco',                  2002, 'Coraline descobre uma porta secreta que leva a uma versão paralela de sua vida.', 'P5-E1'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 13);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 14, 'Memórias Póstumas de Brás Cubas', '9788535902826', 'Companhia das Letras', 1881, 'Brás Cubas narra suas memórias após a morte, em uma sátira brilhante da sociedade carioca.', 'P5-E2'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 14);

INSERT INTO Livros (id_livro, nome_livro, isnb, editora, ano, sinopse, localizacao_fisica) OVERRIDING SYSTEM VALUE
SELECT 15, 'O Diário de Anne Frank', '9788501043674', 'Record',               1947, 'O diário de uma menina judia durante a ocupação nazista na Segunda Guerra Mundial.', 'P5-E3'
WHERE NOT EXISTS (SELECT 1 FROM Livros WHERE id_livro = 15);

-- ============================================================
-- 6. LIVRO_AUTOR (junction — no identity columns)
-- ============================================================
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 1,  1  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 1  AND id_autor = 1);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 2,  2  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 2  AND id_autor = 2);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 3,  3  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 3  AND id_autor = 3);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 4,  4  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 4  AND id_autor = 4);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 5,  5  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 5  AND id_autor = 5);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 6,  6  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 6  AND id_autor = 6);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 7,  7  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 7  AND id_autor = 7);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 8,  8  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 8  AND id_autor = 8);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 9,  10 WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 9  AND id_autor = 10);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 10, 4  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 10 AND id_autor = 4);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 11, 9  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 11 AND id_autor = 9);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 12, 11 WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 12 AND id_autor = 11);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 13, 10 WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 13 AND id_autor = 10);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 14, 1  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 14 AND id_autor = 1);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 15, 12 WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 15 AND id_autor = 12);

-- Books with multiple authors
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 1,  2  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 1  AND id_autor = 2);
INSERT INTO Livro_Autor (id_livro, id_autor) SELECT 14, 7  WHERE NOT EXISTS (SELECT 1 FROM Livro_Autor WHERE id_livro = 14 AND id_autor = 7);

-- ============================================================
-- 7. GENERO_LIVRO (junction — no identity columns)
-- ============================================================
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 1  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 1);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 2, 1  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 2 AND id_livro = 1);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 2  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 2);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 2, 3  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 2 AND id_livro = 3);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 4  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 4);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 3, 5  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 3 AND id_livro = 5);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 2, 6  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 2 AND id_livro = 6);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 6  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 6);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 7  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 7);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 8, 8  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 8 AND id_livro = 8);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 4, 9  WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 4 AND id_livro = 9);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 10 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 10);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 6, 11 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 6 AND id_livro = 11);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 12 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 12);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 3, 13 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 3 AND id_livro = 13);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 4, 13 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 4 AND id_livro = 13);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 1, 14 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 1 AND id_livro = 14);
INSERT INTO Genero_livro (id_genero, id_livro) SELECT 7, 15 WHERE NOT EXISTS (SELECT 1 FROM Genero_livro WHERE id_genero = 7 AND id_livro = 15);

-- ============================================================
-- 8. ESTOQUE (FK → id_livro — not the PK identity column)
-- ============================================================
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 1,  10, 1, 0, 0, 2  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 1);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 2,  8,  0, 1, 0, 2  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 2);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 3,  15, 2, 2, 1, 3  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 3);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 4,  12, 0, 0, 0, 2  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 4);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 5,  20, 3, 5, 1, 5  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 5);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 6,  7,  1, 0, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 6);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 7,  5,  0, 1, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 7);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 8,  6,  0, 0, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 8);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 9,  4,  0, 1, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 9);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 10, 11, 0, 2, 0, 2  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 10);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 11, 3,  0, 0, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 11);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 12, 14, 0, 0, 0, 3  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 12);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 13, 6,  1, 0, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 13);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 14, 9,  0, 0, 0, 2  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 14);
INSERT INTO Estoque (id_livro, quantidade_total, quantidade_reservada, quantidade_emprestada, quantidade_danificada, estoque_minimo)
SELECT 15, 7,  0, 0, 0, 1  WHERE NOT EXISTS (SELECT 1 FROM Estoque WHERE id_livro = 15);

-- ============================================================
-- 9. FOTOS (FK → id_livro — no identity in column list)
-- ============================================================
INSERT INTO Fotos (id_livro, foto)
SELECT 1, 'data:image/png;base64,PLACEHOLDER_DOM_CASMURRO'
WHERE NOT EXISTS (SELECT 1 FROM Fotos WHERE id_livro = 1);

-- ============================================================
-- 10. RESERVAS
-- ============================================================
INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 1, 1, 5, 1, NULL, '2024-06-01', '2024-06-08', '2024-06-03', '2024-07-03', NULL, 'emprestado', 'RSV-001'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-001');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 2, 2, 3, 2, 2, '2024-05-10', '2024-05-17', '2024-05-12', '2024-06-12', '2024-06-10', 'devolvido', 'RSV-002'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-002');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 3, 3, 7, NULL, NULL, '2024-06-15', '2024-06-22', NULL, NULL, NULL, 'reservado', 'RSV-003'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-003');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 4, 1, 10, 1, 1, '2024-04-01', '2024-04-08', '2024-04-02', '2024-05-02', '2024-05-01', 'devolvido', 'RSV-004'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-004');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 5, 4, 5, 2, NULL, '2024-06-10', '2024-06-17', '2024-06-12', '2024-07-12', NULL, 'emprestado', 'RSV-005'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-005');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 6, 5, 5, 1, NULL, '2024-06-12', '2024-06-19', '2024-06-14', '2024-07-14', NULL, 'emprestado', 'RSV-006'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-006');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 7, 6, 9, NULL, NULL, '2024-06-20', '2024-06-27', NULL, NULL, NULL, 'cancelado', 'RSV-007'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-007');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 8, 7, 2, NULL, NULL, '2024-06-25', '2024-07-02', NULL, NULL, NULL, 'reservado', 'RSV-008'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-008');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 9, 2, 1, 2, NULL, '2024-05-20', '2024-05-27', '2024-05-22', '2024-06-22', NULL, 'emprestado', 'RSV-009'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-009');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 10, 8, 6, 1, NULL, '2024-06-28', '2024-07-05', '2024-06-30', '2024-07-30', NULL, 'emprestado', 'RSV-010'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-010');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 11, 1, 3, 1, NULL, '2024-06-10', '2024-06-17', '2024-06-11', '2024-07-11', NULL, 'emprestado', 'RSV-011'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-011');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 12, 3, 5, NULL, NULL, '2024-06-30', '2024-07-07', NULL, NULL, NULL, 'reservado', 'RSV-012'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-012');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 13, 9, 13, NULL, NULL, '2024-07-01', '2024-07-08', NULL, NULL, NULL, 'reservado', 'RSV-013'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-013');

INSERT INTO Reservas (id_reserva, id_cliente, id_livro, id_funcionario_retirada, id_funcionario_devolucao, data_reserva, data_limite_retirada, data_retirada, data_prevista_devolucao, data_devolucao, status_reserva, codigo_reserva) OVERRIDING SYSTEM VALUE
SELECT 14, 10, 5, NULL, NULL, '2024-07-02', '2024-07-09', NULL, NULL, NULL, 'reservado', 'RSV-014'
WHERE NOT EXISTS (SELECT 1 FROM Reservas WHERE codigo_reserva = 'RSV-014');

-- ============================================================
-- 11. MULTAS (FK → id_reserva — no identity in column list)
-- ============================================================
INSERT INTO Multas (id_reserva, valor_multa, data_multa, status_multa, data_pagamento)
SELECT 2, 5.00, '2024-06-10', 'pago', '2024-06-11'
WHERE NOT EXISTS (SELECT 1 FROM Multas WHERE id_reserva = 2);

INSERT INTO Multas (id_reserva, valor_multa, data_multa, status_multa, data_pagamento)
SELECT 4, 3.50, '2024-05-02', 'pago', '2024-05-05'
WHERE NOT EXISTS (SELECT 1 FROM Multas WHERE id_reserva = 4);

INSERT INTO Multas (id_reserva, valor_multa, data_multa, status_multa, data_pagamento)
SELECT 9, 8.00, '2024-06-23', 'pendente', NULL
WHERE NOT EXISTS (SELECT 1 FROM Multas WHERE id_reserva = 9);

INSERT INTO Multas (id_reserva, valor_multa, data_multa, status_multa, data_pagamento)
SELECT 11, 2.00, '2024-07-12', 'pendente', NULL
WHERE NOT EXISTS (SELECT 1 FROM Multas WHERE id_reserva = 11);

-- ============================================================
-- 12. MOVIMENTACAO_ESTOQUE (FK → id_livro — no identity)
-- ============================================================
INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 5, 'E', 20, '2024-01-15', 'Compra inicial - Lote 001'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 5 AND tipo_movimentacao = 'E' AND motivo LIKE '%Lote 001%');

INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 1, 'E', 10, '2024-01-10', 'Compra inicial'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 1 AND tipo_movimentacao = 'E' AND data_movimentacao = '2024-01-10');

INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 3, 'E', 15, '2024-01-15', 'Compra inicial'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 3 AND tipo_movimentacao = 'E' AND data_movimentacao = '2024-01-15');

INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 5, 'A', 5, '2024-03-01', 'Reposição de estoque'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 5 AND tipo_movimentacao = 'A' AND data_movimentacao = '2024-03-01');

INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 3, 'X', 1, '2024-04-10', 'Livro danificado por água'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 3 AND tipo_movimentacao = 'X' AND data_movimentacao = '2024-04-10');

INSERT INTO Movimentacao_estoque (id_livro, tipo_movimentacao, quantidade, data_movimentacao, motivo)
SELECT 5, 'E', 5, '2024-05-01', 'Compra adicional - Lote 002'
WHERE NOT EXISTS (SELECT 1 FROM Movimentacao_estoque WHERE id_livro = 5 AND tipo_movimentacao = 'E' AND motivo LIKE '%Lote 002%');

-- ============================================================
-- 13. LOGS
-- ============================================================
INSERT INTO Logs (id_cliente, id_funcionario, acao, data_acao, ip)
SELECT 1, NULL, 'Login de cliente (id: 1)',        '2024-06-01 10:30:00', '127.0.0.1'
WHERE NOT EXISTS (SELECT 1 FROM Logs WHERE acao = 'Login de cliente (id: 1)' AND data_acao = '2024-06-01 10:30:00');

INSERT INTO Logs (id_cliente, id_funcionario, acao, data_acao, ip)
SELECT NULL, 1, 'Login de funcionario (id: 1)',    '2024-06-01 09:00:00', '127.0.0.1'
WHERE NOT EXISTS (SELECT 1 FROM Logs WHERE acao = 'Login de funcionario (id: 1)' AND data_acao = '2024-06-01 09:00:00');

INSERT INTO Logs (id_cliente, id_funcionario, acao, data_acao, ip)
SELECT 1, NULL, 'Realizou reserva (id: 1)',        '2024-06-01 10:35:00', '192.168.1.100'
WHERE NOT EXISTS (SELECT 1 FROM Logs WHERE acao = 'Realizou reserva (id: 1)' AND data_acao = '2024-06-01 10:35:00');

-- ============================================================
-- 14. HISTORICO_CLIENTE
-- ============================================================
INSERT INTO Historico_cliente (id_cliente, campo_alterado, valor_antigo, valor_novo, data_alteracao)
SELECT 1, 'endereco', 'Rua Antiga, 0', 'Rua das Letras, 100', '2024-02-01'
WHERE NOT EXISTS (SELECT 1 FROM Historico_cliente WHERE id_cliente = 1 AND campo_alterado = 'endereco' AND data_alteracao = '2024-02-01');

INSERT INTO Historico_cliente (id_cliente, campo_alterado, valor_antigo, valor_novo, data_alteracao)
SELECT 2, 'telefone', '11977777777', '11888888888', '2024-03-15'
WHERE NOT EXISTS (SELECT 1 FROM Historico_cliente WHERE id_cliente = 2 AND campo_alterado = 'telefone' AND data_alteracao = '2024-03-15');

INSERT INTO Historico_cliente (id_cliente, campo_alterado, valor_antigo, valor_novo, data_alteracao)
SELECT 1, 'email', 'cliente.velho@email.com', 'cliente@biblioteca.com', '2024-01-20'
WHERE NOT EXISTS (SELECT 1 FROM Historico_cliente WHERE id_cliente = 1 AND campo_alterado = 'email' AND data_alteracao = '2024-01-20');

-- ============================================================
-- Sync identity sequences after OVERRIDING SYSTEM VALUE inserts
-- ============================================================
SELECT setval('autores_id_autor_seq',            (SELECT COALESCE(MAX(id_autor), 0)            FROM Autores));
SELECT setval('generos_id_genero_seq',           (SELECT COALESCE(MAX(id_genero), 0)           FROM Generos));
SELECT setval('funcionarios_id_funcionario_seq', (SELECT COALESCE(MAX(id_funcionario), 0)      FROM Funcionarios));
SELECT setval('clientes_id_cliente_seq',         (SELECT COALESCE(MAX(id_cliente), 0)          FROM Clientes));
SELECT setval('livros_id_livro_seq',             (SELECT COALESCE(MAX(id_livro), 0)            FROM Livros));
SELECT setval('fotos_id_foto_seq',               (SELECT COALESCE(MAX(id_foto), 0)             FROM Fotos));
SELECT setval('estoque_id_estoque_seq',          (SELECT COALESCE(MAX(id_estoque), 0)          FROM Estoque));
SELECT setval('reservas_id_reserva_seq',         (SELECT COALESCE(MAX(id_reserva), 0)          FROM Reservas));
SELECT setval('multas_id_multa_seq',             (SELECT COALESCE(MAX(id_multa), 0)            FROM Multas));
SELECT setval('movimentacao_estoque_id_movimentacao_seq', (SELECT COALESCE(MAX(id_movimentacao), 0) FROM Movimentacao_estoque));
SELECT setval('logs_id_log_seq',                 (SELECT COALESCE(MAX(id_log), 0)              FROM Logs));
SELECT setval('historico_cliente_id_historico_seq', (SELECT COALESCE(MAX(id_historico), 0)     FROM Historico_cliente));
