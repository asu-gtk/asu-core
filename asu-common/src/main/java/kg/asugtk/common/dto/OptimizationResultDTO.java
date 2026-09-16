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
public class OptimizationResultDTO {
    private boolean optimalSolutionFound;
    private double totalEstimatedCostRubles;
    private double totalPlannedTons;
    private List<TruckAllocationDTO> allocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TruckAllocationDTO {
        private String excavatorCode;
        private String truckModel;
        private double requiredTrucksCount;
        private int integerAssignedTrucks;
        private double estimatedTripsPerShift;
        private double estimatedVolumeTons;
        private double estimatedCostRubles;
    }
}
