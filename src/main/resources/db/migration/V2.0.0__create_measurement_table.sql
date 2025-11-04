
-- Cria uma tabela para a coleta de dados
CREATE TABLE IF NOT EXISTS measurement (
    ts TIMESTAMPTZ NOT NULL,
    sensor_id INTEGER NOT NULL REFERENCES sensor(sensor_id) ON DELETE RESTRICT,

    -- Os valores medidos
    temperature DOUBLE PRECISION NULL,
    humidity DOUBLE PRECISION NULL,

    -- A chave primária
    PRIMARY KEY (sensor_id, ts),

    -- Garante que pelo menos uma das medições esteja preenchida
    CONSTRAINT chk_at_least_one_value
            CHECK (temperature IS NOT NULL OR humidity IS NOT NULL)
);

-- Cria a hypertable
SELECT create_hypertable(
    'measurement',
    'ts',
    create_default_indexes => FALSE
);

CREATE INDEX IF NOT EXISTS idx_sensor_time_desc
    ON measurement(sensor_id, ts DESC);
