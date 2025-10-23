
CREATE TABLE sensors (
    sensor_id SERIAL PRIMARY KEY,
    sensor_name TEXT NOT NULL UNIQUE,
    sensor_type TEXT NOT NULL,
    location TEXT,
    unique_address TEXT NULL
);

CREATE TABLE measurements (
    measurement_id BIGSERIAL NOT NULL,
    time TIMESTAMPTZ NOT NULL,
    sensor_id INTEGER NOT NULL REFERENCES sensors(sensor_id) ON DELETE CASCADE,
    temperature DOUBLE PRECISION NOT NULL,
    humidity DOUBLE PRECISION NULL
);

SELECT create_hypertable('measurements', 'time');

CREATE INDEX ON measurements (sensor_id, "time" DESC);