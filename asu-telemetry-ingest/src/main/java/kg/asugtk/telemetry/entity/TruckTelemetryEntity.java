package kg.asugtk.telemetry.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Гипертаблица TimescaleDB: одна строка = один телеметрический пакет от самосвала.
 * Партиционирование по времени выполняется через миграцию Flyway (V3__timescale_hypertable.sql).
 */
@Entity
@Table(name = "truck_telemetry",
        indexes = {
            @Index(name = "idx_telemetry_truck_time", columnList = "truck_id, captured_at DESC"),
            @Index(name = "idx_telemetry_time",       columnList = "captured_at DESC")
        })
public class TruckTelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telemetry_seq")
    @SequenceGenerator(name = "telemetry_seq", sequenceName = "truck_telemetry_seq", allocationSize = 100)
    private Long id;

    @Column(name = "truck_id", nullable = false, length = 32)
    private String truckId;

    @Column(name = "truck_number", length = 16)
    private String truckNumber;

    @Column(name = "driver_name", length = 128)
    private String driverName;

    @Column(name = "captured_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant capturedAt;

    @Column(name = "lat", nullable = false)
    private double lat;

    @Column(name = "lon", nullable = false)
    private double lon;

    @Column(name = "speed_kmh")
    private double speedKmh;

    @Column(name = "heading_deg")
    private double headingDeg;

    @Column(name = "fuel_liters")
    private double fuelLiters;

    @Column(name = "load_weight_tons")
    private double loadWeightTons;

    @Column(name = "status", length = 32)
    private String status;

    // --- getters / setters ---

    public Long getId() { return id; }

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public String getTruckNumber() { return truckNumber; }
    public void setTruckNumber(String truckNumber) { this.truckNumber = truckNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Instant getCapturedAt() { return capturedAt; }
    public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getSpeedKmh() { return speedKmh; }
    public void setSpeedKmh(double speedKmh) { this.speedKmh = speedKmh; }

    public double getHeadingDeg() { return headingDeg; }
    public void setHeadingDeg(double headingDeg) { this.headingDeg = headingDeg; }

    public double getFuelLiters() { return fuelLiters; }
    public void setFuelLiters(double fuelLiters) { this.fuelLiters = fuelLiters; }

    public double getLoadWeightTons() { return loadWeightTons; }
    public void setLoadWeightTons(double loadWeightTons) { this.loadWeightTons = loadWeightTons; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
