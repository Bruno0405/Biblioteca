-- V11: Add ON DELETE CASCADE to allow cascaded deletion of related records

-- Multas: when a reserva is deleted, delete associated multa
ALTER TABLE Multas
    DROP CONSTRAINT IF EXISTS multas_id_reserva_fkey,
    ADD CONSTRAINT multas_id_reserva_fkey
        FOREIGN KEY (id_reserva) REFERENCES Reservas(id_reserva) ON DELETE CASCADE;

-- Reservas: when a cliente is deleted, delete all their reservas (and multas via above cascade)
ALTER TABLE Reservas
    DROP CONSTRAINT IF EXISTS reservas_id_cliente_fkey,
    ADD CONSTRAINT reservas_id_cliente_fkey
        FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE;

-- Historico_cliente: when a cliente is deleted, delete their history
ALTER TABLE Historico_cliente
    DROP CONSTRAINT IF EXISTS historico_cliente_id_cliente_fkey,
    ADD CONSTRAINT historico_cliente_id_cliente_fkey
        FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE;

-- Logs: when a cliente is deleted, keep the log but nullify the client reference
ALTER TABLE Logs
    DROP CONSTRAINT IF EXISTS logs_id_cliente_fkey,
    ADD CONSTRAINT logs_id_cliente_fkey
        FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE SET NULL;
