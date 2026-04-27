-- V4: Corrige discrepâncias entre as migrations iniciais e o schema definitivo
--     Referência: biblioteca03.sql

-- ── Livros ──────────────────────────────────────────────────────────────────
-- data_adicao não existe no schema definitivo e não tem DEFAULT → bloqueia todo INSERT
ALTER TABLE Livros DROP COLUMN IF EXISTS data_adicao;

-- ── Reservas ─────────────────────────────────────────────────────────────────
-- data_reserva NOT NULL sem DEFAULT → reserva criada hoje por padrão
ALTER TABLE Reservas ALTER COLUMN data_reserva SET DEFAULT CURRENT_DATE;

-- ── Multas ────────────────────────────────────────────────────────────────────
ALTER TABLE Multas ALTER COLUMN data_multa     SET DEFAULT CURRENT_DATE;
ALTER TABLE Multas ALTER COLUMN status_multa   SET DEFAULT 'pendente';

-- ── Histórico de cliente ──────────────────────────────────────────────────────
-- Amplia campo_alterado para VARCHAR ilimitado (schema definitivo não usa dm_padrao aqui)
ALTER TABLE Historico_cliente ALTER COLUMN campo_alterado TYPE VARCHAR;
ALTER TABLE Historico_cliente ALTER COLUMN data_alteracao SET DEFAULT CURRENT_DATE;

-- ── Movimentação de estoque ───────────────────────────────────────────────────
ALTER TABLE Movimentacao_estoque ALTER COLUMN data_movimentacao SET DEFAULT CURRENT_DATE;
ALTER TABLE Movimentacao_estoque ALTER COLUMN motivo            SET DEFAULT '';
ALTER TABLE Movimentacao_estoque ALTER COLUMN tipo_movimentacao SET DEFAULT 'E';

-- ── Estoque ───────────────────────────────────────────────────────────────────
ALTER TABLE Estoque ALTER COLUMN estoque_minimo SET DEFAULT 0;
