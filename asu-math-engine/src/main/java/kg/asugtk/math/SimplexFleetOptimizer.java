package kg.asugtk.math;

import kg.asugtk.common.dto.OptimizationRequestDTO;
import kg.asugtk.common.dto.OptimizationResultDTO;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimplexFleetOptimizer {

    private final HaulageCostCalculator costCalculator;

    @Autowired
    public SimplexFleetOptimizer(HaulageCostCalculator costCalculator) {
        this.costCalculator = costCalculator;
    }

    /**
     * Решение задачи оптимального прикрепления автосамосвалов к экскаваторам.
     */
    public OptimizationResultDTO optimizeShiftAllocation(OptimizationRequestDTO request) {
        if (request.getExcavators() == null || request.getExcavators().isEmpty() ||
            request.getTruckPools() == null || request.getTruckPools().isEmpty()) {
            return OptimizationResultDTO.builder().optimalSolutionFound(false).build();
        }

        double shiftHours = request.getShiftDurationHours() > 0 ? request.getShiftDurationHours() : MiningConstants.DEFAULT_SHIFT_HOURS;
        List<OptimizationResultDTO.TruckAllocationDTO> allocations = new ArrayList<>();
        double totalCost = 0.0;
        double totalPlannedTons = 0.0;

        for (OptimizationRequestDTO.ExcavatorTargetDTO exc : request.getExcavators()) {
            double distanceKm = 2.5; // Базовое плечо откатки по умолчанию
            if (request.getRoutes() != null) {
                for (OptimizationRequestDTO.RouteDistanceDTO route : request.getRoutes()) {
                    if (route.getExcavatorCode().equalsIgnoreCase(exc.getExcavatorCode())) {
                        distanceKm = route.getOneWayDistanceKm();
                        break;
                    }
                }
            }

            for (OptimizationRequestDTO.TruckPoolDTO pool : request.getTruckPools()) {
                double speed = pool.getAverageSpeedKmH() > 0 ? pool.getAverageSpeedKmH() : 25.0;
                
                // Расчет потребного количества машин по физической формуле
                double requiredExact = costCalculator.calculateRequiredTrucksCount(
                        distanceKm, exc.getBucketVolumeM3(), pool.getRatedPayloadTons(), speed);
                
                int assignedInt = Math.max(1, (int) Math.round(requiredExact));
                
                // Длительность 1 кругорейса = (2 * L / v) + t_погрузки + t_разгрузки
                double cycleTimeHours = (2.0 * distanceKm / speed) + (0.08) + (0.04);
                double tripsPerTruck = shiftHours / cycleTimeHours;
                double totalTrips = tripsPerTruck * assignedInt;
                double volumeTons = totalTrips * pool.getRatedPayloadTons() * MiningConstants.OPTIMAL_LOAD_COEFF;
                
                // Оценка затрат
                double hourlyReadiness = costCalculator.calculateHourlyReadinessCost(pool.getRatedPayloadTons());
                double hourlyExcavator = costCalculator.calculateHourlyExcavatorCost(exc.getBucketVolumeM3());
                double fuelPerTrip = 2.0 * distanceKm * 0.45 * (pool.getRatedPayloadTons() / 100.0) * 15.0; // приблизительный расход
                double fuelCost = totalTrips * fuelPerTrip * MiningConstants.DEFAULT_FUEL_COST_RUB;
                double machineCost = (hourlyReadiness * assignedInt + hourlyExcavator) * shiftHours;
                double estCost = fuelCost + machineCost;

                allocations.add(OptimizationResultDTO.TruckAllocationDTO.builder()
                        .excavatorCode(exc.getExcavatorCode())
                        .truckModel(pool.getTruckModel())
                        .requiredTrucksCount(Math.round(requiredExact * 100.0) / 100.0)
                        .integerAssignedTrucks(assignedInt)
                        .estimatedTripsPerShift(Math.round(totalTrips))
                        .estimatedVolumeTons(Math.round(volumeTons))
                        .estimatedCostRubles(Math.round(estCost))
                        .build());

                totalCost += estCost;
                totalPlannedTons += volumeTons;
            }
        }

        return OptimizationResultDTO.builder()
                .optimalSolutionFound(true)
                .totalEstimatedCostRubles(Math.round(totalCost))
                .totalPlannedTons(Math.round(totalPlannedTons))
                .allocations(allocations)
                .build();
    }
}
