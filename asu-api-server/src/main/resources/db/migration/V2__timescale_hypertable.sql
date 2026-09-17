-- V2__timescale_hypertable.sql
-- Активируется только если на сервере установлен TimescaleDB.
-- В dev-режиме (H2) эта миграция игнорируется через conditional guard.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_extension
        WHERE extname = 'timescaledb'
    ) THEN
        PERFORM create_hypertable(
            'truck_telemetry',
            'captured_at',
            chunk_time_interval => INTERVAL '1 day',
            if_not_exists        => TRUE
        );

        -- Политика сжатия: сжимать чанки старше 7 дней
        PERFORM add_compression_policy('truck_telemetry', INTERVAL '7 days', if_not_exists => TRUE);

        -- Политика удержания: хранить последние 90 дней телеметрии
        PERFORM add_retention_policy('truck_telemetry', INTERVAL '90 days', if_not_exists => TRUE);
    END IF;
END $$;
