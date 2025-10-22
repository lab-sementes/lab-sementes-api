-- Tabela de metadados dos sensores
CREATE TABLE sensors (
    sensor_id SERIAL PRIMARY KEY,
    sensor_name TEXT NOT NULL UNIQUE,
    sensor_type TEXT NOT NULL CHECK (sensor_type IN ('DHT11', 'DS18B20')),
    location TEXT,
    unique_address TEXT NULL
);

-- Hypertable para armazenar as medições
CREATE TABLE measurements (
    -- CORREÇÃO: Removemos "PRIMARY KEY" daqui para satisfazer o TimescaleDB.
    -- O Panache ainda usará esta coluna como seu "@Id"
    measurement_id BIGSERIAL NOT NULL,
    "time" TIMESTAMPTZ NOT NULL,
    sensor_id INTEGER NOT NULL REFERENCES sensors(sensor_id) ON DELETE CASCADE,
    temperature DOUBLE PRECISION NOT NULL,
    humidity DOUBLE PRECISION NULL
);

-- Agora este comando vai funcionar, pois não há chaves primárias ou
-- índices únicos na tabela 'measurements' que o impeçam.
SELECT create_hypertable('measurements', 'time');

-- Este índice NÃO é único, então não há problema em criá-lo.
-- Ele é essencial para buscas rápidas.
CREATE INDEX ON measurements (sensor_id, "time" DESC);