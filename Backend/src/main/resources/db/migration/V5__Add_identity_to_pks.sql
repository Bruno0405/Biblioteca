-- V5: Adiciona GENERATED ALWAYS AS IDENTITY nas PKs que foram criadas como
--     INT NOT NULL simples (schema criado manualmente via biblioteca03.sql).
--     Só executa se a coluna ainda não tiver identity — seguro em qualquer cenário.

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='autores' AND column_name='id_autor' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Autores ALTER COLUMN id_autor ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='livros' AND column_name='id_livro' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Livros ALTER COLUMN id_livro ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='funcionarios' AND column_name='id_funcionario' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Funcionarios ALTER COLUMN id_funcionario ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='clientes' AND column_name='id_cliente' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Clientes ALTER COLUMN id_cliente ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='generos' AND column_name='id_genero' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Generos ALTER COLUMN id_genero ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='fotos' AND column_name='id_foto' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Fotos ALTER COLUMN id_foto ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='historico_cliente' AND column_name='id_historico' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Historico_cliente ALTER COLUMN id_historico ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='estoque' AND column_name='id_estoque' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Estoque ALTER COLUMN id_estoque ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='movimentacao_estoque' AND column_name='id_movimentacao' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Movimentacao_estoque ALTER COLUMN id_movimentacao ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='reservas' AND column_name='id_reserva' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Reservas ALTER COLUMN id_reserva ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='multas' AND column_name='id_multa' AND identity_generation IS NOT NULL) THEN
        ALTER TABLE Multas ALTER COLUMN id_multa ADD GENERATED ALWAYS AS IDENTITY (START WITH 100);
    END IF;
END $$;
