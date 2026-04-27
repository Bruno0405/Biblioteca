-- V7: Amplia precisão de valor_multa para suportar valores maiores que 999.99
ALTER TABLE Multas ALTER COLUMN valor_multa TYPE DECIMAL(10,2);
