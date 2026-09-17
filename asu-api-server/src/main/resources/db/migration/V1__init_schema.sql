-- V1__init_schema.sql
CREATE TABLE IF NOT EXISTS truck_telemetry (
    id               BIGSERIAL           NOT NULL,
    truck_id         VARCHAR(32)         NOT NULL,
    truck_number     VARCHAR(16),
    driver_name      VARCHAR(128),
    captured_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    lat              DOUBLE PRECISION    NOT NULL,
    lon              DOUBLE PRECISION    NOT NULL,
    speed_kmh        DOUBLE PRECISION    DEFAULT 0,
    heading_deg      DOUBLE PRECISION    DEFAULT 0,
    fuel_liters      DOUBLE PRECISION    DEFAULT 0,
    load_weight_tons DOUBLE PRECISION    DEFAULT 0,
    status           VARCHAR(32),
    PRIMARY KEY (id, captured_at)
);

CREATE INDEX IF NOT EXISTS idx_telemetry_truck_time ON truck_telemetry (truck_id, captured_at DESC);
CREATE INDEX IF NOT EXISTS idx_telemetry_time       ON truck_telemetry (captured_at DESC);
