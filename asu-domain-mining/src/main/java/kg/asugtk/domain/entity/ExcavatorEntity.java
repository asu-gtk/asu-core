package kg.asugtk.domain.entity;

import jakarta.persistence.*;
import kg.asugtk.common.model.VehicleStatus;
import lombok.*;

@Entity
@Table(name = "excavators")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcavatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g., "EXC-02", "EXC-07"

    @Column(nullable = false, length = 100)
    private String model; // e.g., "ЭКГ-5А", "Hitachi EX3600"

    @Column(nullable = false)
    private double bucketVolumeM3; // e.g., 5.0 m3

    @Column(nullable = false)
    private double maxHourlyCapacityTons; // e.g., 600 t/h

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleStatus status;

    private Double locationLatitude;
    private Double locationLongitude;
}
