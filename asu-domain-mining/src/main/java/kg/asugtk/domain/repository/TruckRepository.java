package kg.asugtk.domain.repository;

import kg.asugtk.domain.entity.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<TruckEntity, Long> {
    Optional<TruckEntity> findByCode(String code);
}
