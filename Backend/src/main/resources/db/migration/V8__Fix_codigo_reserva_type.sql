-- V8: Muda codigo_reserva de INT para VARCHAR para suportar códigos alfanuméricos (ex: RSV-KFYB3D)
ALTER TABLE Reservas ALTER COLUMN codigo_reserva TYPE VARCHAR(20) USING codigo_reserva::TEXT;
