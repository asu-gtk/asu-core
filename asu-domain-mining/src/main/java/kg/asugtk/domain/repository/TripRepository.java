package kg.asugtk.domain.repository;

import kg.asugtk.domain.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

    @Query("SELECT t FROM TripEntity t WHERE t.loadStartTime >= :startTime AND t.loadStartTime <= :endTime")
    List<TripEntity> findTripsInTimeRange(Instant startTime, Instant endTime);

    List<TripEntity> findByTruckCode(String truckCode);
}
