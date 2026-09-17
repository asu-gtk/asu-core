package kg.asugtk.telemetry.repository;

import kg.asugtk.telemetry.entity.TruckTelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TruckTelemetryRepository extends JpaRepository<TruckTelemetryEntity, Long> {

    /** Последние N позиций конкретного борта (для трека на карте диспетчера) */
    List<TruckTelemetryEntity> findTop200ByTruckIdOrderByCapturedAtDesc(String truckId);

    /** Текущая позиция каждого борта (последняя запись из каждой группы) */
    @Query(value = """
            SELECT DISTINCT ON (truck_id) *
            FROM truck_telemetry
            ORDER BY truck_id, captured_at DESC
            """, nativeQuery = true)
    List<TruckTelemetryEntity> findLatestPositionPerTruck();

    /** История за период для отчета */
    @Query("SELECT t FROM TruckTelemetryEntity t WHERE t.capturedAt BETWEEN :from AND :to ORDER BY t.capturedAt ASC")
    List<TruckTelemetryEntity> findByPeriod(@Param("from") Instant from, @Param("to") Instant to);

    /** Средняя скорость по борту за диапазон (для KPI смены) */
    @Query(value = """
            SELECT AVG(speed_kmh)
            FROM truck_telemetry
            WHERE truck_id = :truckId
              AND captured_at BETWEEN :from AND :to
            """, nativeQuery = true)
    Double avgSpeedByTruckAndPeriod(
        @Param("truckId") String truckId,
        @Param("from") Instant from,
        @Param("to") Instant to
    );
}
