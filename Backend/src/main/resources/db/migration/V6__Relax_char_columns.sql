-- V6: Relaxa colunas com tipo fixo CHAR que causam erros de "valor muito longo"

-- isnb: ISBN pode ter 10, 13 ou mais chars dependendo do formato inserido
ALTER TABLE Livros ALTER COLUMN isnb TYPE VARCHAR(20);

-- status_reserva e status_multa: CHAR de tamanho fixo causa padding e rejeição
ALTER TABLE Reservas ALTER COLUMN status_reserva TYPE VARCHAR(15);
ALTER TABLE Multas   ALTER COLUMN status_multa   TYPE VARCHAR(20);

-- tipo_movimentacao: mantém CHAR(1) mas garante que o CHECK ainda vale
-- (já estava correto, sem alteração necessária)

-- perfil de funcionario: CHAR(1) correto, sem alteração
