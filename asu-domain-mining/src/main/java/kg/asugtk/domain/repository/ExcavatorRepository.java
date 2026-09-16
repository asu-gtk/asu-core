package kg.asugtk.domain.repository;

import kg.asugtk.domain.entity.ExcavatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcavatorRepository extends JpaRepository<ExcavatorEntity, Long> {
    Optional<ExcavatorEntity> findByCode(String code);
}
