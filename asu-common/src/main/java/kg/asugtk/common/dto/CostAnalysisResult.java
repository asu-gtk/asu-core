package kg.asugtk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostAnalysisResult {
    private String objectCode;
    private long totalTrips;
    private double totalDistanceKm;
    private double totalTransportedTons;
    private double totalTonKilometers;
    
    private double fuelConsumedLiters;
    private double fuelCostRubles;
    private double vehicleReadinessCostRubles;
    private double excavatorOperationCostRubles;
    private double totalCostRubles;
    
    /**
     * Себестоимость перевозки 1 тонно-километра: TotalCost / TotalTonKilometers (руб / (т * км))
     */
    private double costPerTonKm;
    
    /**
     * Себестоимость перевозки 1 тонны руды/вскрыши: TotalCost / TotalTransportedTons (руб / т)
     */
    private double costPerTon;
}
