package kg.asugtk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRequestDTO {
    private double shiftDurationHours;
    private List<ExcavatorTargetDTO> excavators;
    private List<TruckPoolDTO> truckPools;
    private List<RouteDistanceDTO> routes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcavatorTargetDTO {
        private String excavatorCode;
        private double bucketVolumeM3;
        private double planVolumeTons;
        private double maxHourlyCapacityTons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TruckPoolDTO {
        private String truckModel;
        private int availableCount;
        private double ratedPayloadTons;
        private double emptyWeightTons;
        private double averageSpeedKmH;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteDistanceDTO {
        private String excavatorCode;
        private String unloadPointCode;
        private double oneWayDistanceKm;
    }
}
