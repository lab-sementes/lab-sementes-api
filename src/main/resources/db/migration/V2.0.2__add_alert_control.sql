ALTER TABLE sensor
ADD COLUMN data_ultimo_alerta TIMESTAMPTZ,
ADD COLUMN intervalo_minutos_alerta INTEGER DEFAULT 60;