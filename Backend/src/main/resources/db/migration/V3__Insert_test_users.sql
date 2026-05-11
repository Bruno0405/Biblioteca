INSERT INTO Funcionarios (nome, email, senha, perfil)
SELECT 'Administrador Teste', 'admin@biblioteca.com', '123', 'A'
WHERE NOT EXISTS (
    SELECT 1 FROM Funcionarios WHERE email = 'admin@biblioteca.com'
);

INSERT INTO Funcionarios (nome, email, senha, perfil)
SELECT 'Atendente Teste', 'atendente@biblioteca.com', '123', 'U'
WHERE NOT EXISTS (
    SELECT 1 FROM Funcionarios WHERE email = 'atendente@biblioteca.com'
);

INSERT INTO Clientes (
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
WHERE NOT EXISTS (
    SELECT 1 FROM Clientes WHERE email = 'cliente@biblioteca.com'
);

INSERT INTO Clientes (
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
WHERE NOT EXISTS (
    SELECT 1 FROM Clientes WHERE email = 'maria@biblioteca.com'
);
