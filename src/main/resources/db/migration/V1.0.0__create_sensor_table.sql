
-- Cria um sensor, sem se preocupar com a Normalização do tipo e address
CREATE TABLE IF NOT EXISTS sensor (
    sensor_id SERIAL PRIMARY KEY,

    sensor_name VARCHAR(255) NOT NULL UNIQUE,
    sensor_type VARCHAR(100) NOT NULL,
    unique_address VARCHAR(100) UNIQUE NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'Ativado',
    data_criacao TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    data_ultima_mudanca_status TIMESTAMPTZ NULL
);