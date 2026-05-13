-- V9: Increase password column sizes for BCrypt hashes (60 chars) and migrate
--     existing plain-text passwords from the seed data to BCrypt hashes.
--     Also fixes atendente perfil from 'U' (undocumented) to 'F' (Funcionario).

ALTER TABLE Clientes ALTER COLUMN senha_cliente TYPE VARCHAR(255);
ALTER TABLE Funcionarios ALTER COLUMN senha TYPE VARCHAR(255);

-- Pre-computed BCrypt hash of '123' with cost 10
-- Generated once at implementation time; all seed users use the same password.
UPDATE Clientes SET senha_cliente = '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW'
WHERE email IN ('cliente@biblioteca.com', 'maria@biblioteca.com')
  AND (senha_cliente NOT LIKE '$2a$%' OR senha_cliente IS NULL);

UPDATE Funcionarios SET senha = '$2a$10$9HU07Iknh9ef3KsnRBNVfOeACAiLXuXHvPfq5mBijPYCb5jPkECSW'
WHERE email IN ('admin@biblioteca.com', 'atendente@biblioteca.com')
  AND (senha NOT LIKE '$2a$%' OR senha IS NULL);

-- Fix atendente perfil: 'U' is not a documented value. The system supports
-- 'A' (Admin), 'G' (Gerente), 'F' (Funcionario).
UPDATE Funcionarios SET perfil = 'F' WHERE email = 'atendente@biblioteca.com' AND perfil = 'U';
