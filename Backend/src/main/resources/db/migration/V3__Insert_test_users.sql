INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil)
SELECT COALESCE(MAX(id_funcionario), 0) + 1, 'Administrador Teste', 'admin@biblioteca.com', '123', 'A'
FROM Funcionarios
WHERE NOT EXISTS (
    SELECT 1 FROM Funcionarios WHERE email = 'admin@biblioteca.com'
);

INSERT INTO Funcionarios (id_funcionario, nome, email, senha, perfil)
SELECT COALESCE(MAX(id_funcionario), 0) + 1, 'Atendente Teste', 'atendente@biblioteca.com', '123', 'U'
FROM Funcionarios
WHERE NOT EXISTS (
    SELECT 1 FROM Funcionarios WHERE email = 'atendente@biblioteca.com'
);

INSERT INTO Clientes (
    id_cliente,
    nome_cliente,
    senha_cliente,
    cpf,
    data_nascimento,
    telefone,
    email,
    endereco,
    bloqueado,
    tentativas_login,
    email_confirmado
)
SELECT
    COALESCE(MAX(id_cliente), 0) + 1,
    'Cliente Teste',
    '123',
    '12345678901',
    DATE '1995-05-10',
    '11999999999',
    'cliente@biblioteca.com',
    'Rua das Letras, 100',
    FALSE,
    0,
    TRUE
FROM Clientes
WHERE NOT EXISTS (
    SELECT 1 FROM Clientes WHERE email = 'cliente@biblioteca.com'
);

INSERT INTO Clientes (
    id_cliente,
    nome_cliente,
    senha_cliente,
    cpf,
    data_nascimento,
    telefone,
    email,
    endereco,
    bloqueado,
    tentativas_login,
    email_confirmado
)
SELECT
    COALESCE(MAX(id_cliente), 0) + 1,
    'Maria Leitora',
    '123',
    '98765432100',
    DATE '2000-08-21',
    '11888888888',
    'maria@biblioteca.com',
    'Avenida Central, 250',
    FALSE,
    0,
    TRUE
FROM Clientes
WHERE NOT EXISTS (
    SELECT 1 FROM Clientes WHERE email = 'maria@biblioteca.com'
);
