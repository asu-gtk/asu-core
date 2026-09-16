package kg.asugtk.domain.entity;

import jakarta.persistence.*;
import kg.asugtk.common.model.TypeOfWork;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "trips")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tripCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false)
    private TruckEntity truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excavator_id", nullable = false)
    private ExcavatorEntity excavator;

    @Column(length = 100)
    private String unloadPointCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeOfWork typeOfWork;

    private Instant loadStartTime;
    private Instant loadEndTime;
    private Instant unloadStartTime;
    private Instant unloadEndTime;

    @Column(nullable = false)
    private double distanceKm;

    @Column(nullable = false)
    private double payloadTons;

    private double fuelAtLoading;
    private double fuelAtUnloading;
    private double fuelConsumed;

    // Расчетные себестоимости и затраты (руб)
    private double fuelCostRubles;
    private double readinessCostRubles;
    private double excavatorCostRubles;
    private double totalTripCostRubles;
    private double costPerTonKm;
}
