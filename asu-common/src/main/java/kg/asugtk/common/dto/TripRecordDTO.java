package kg.asugtk.common.dto;

import kg.asugtk.common.model.TypeOfWork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRecordDTO {
    private String tripId;
    private String truckCode;
    private String truckModel;
    private double truckPayloadRatedTons;
    private double truckWeightEmptyTons;
    
    private String excavatorCode;
    private double excavatorBucketVolumeM3;
    
    private String unloadPointCode;
    private TypeOfWork typeOfWork;
    
    private Instant arrivalLoadingTime;
    private Instant finishLoadingTime;
    private Instant beginUnloadingTime;
    private Instant finishUnloadingTime;
    
    private double tripDistanceKm;
    private double actualWeightTons;
    private double fuelAtLoadingLiters;
    private double fuelAtUnloadingLiters;
    
    private double returnFuelConsumedLiters;
    private double returnDurationHours;
}
