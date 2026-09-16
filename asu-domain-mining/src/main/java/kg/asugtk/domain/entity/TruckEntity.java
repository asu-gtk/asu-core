package kg.asugtk.domain.entity;

import jakarta.persistence.*;
import kg.asugtk.common.model.VehicleStatus;
import lombok.*;

@Entity
@Table(name = "trucks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g., "TRUCK-101"

    @Column(nullable = false, length = 100)
    private String model; // e.g., "БелАЗ-75131"

    @Column(nullable = false)
    private double ratedPayloadTons; // 130.0 t

    @Column(nullable = false)
    private double emptyWeightTons; // 107.0 t

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleStatus status;

    private Double currentLatitude;
    private Double currentLongitude;
    private Double currentFuelLevel;
}
