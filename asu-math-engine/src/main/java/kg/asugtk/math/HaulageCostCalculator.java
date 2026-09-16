package kg.asugtk.math;

import kg.asugtk.common.dto.CostAnalysisResult;
import kg.asugtk.common.dto.TripRecordDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Высокоточный калькулятор прямых эксплуатационных затрат и себестоимости перевозки горной массы.
 */
@Service
public class HaulageCostCalculator {

    /**
     * Расчет удельного путевого расхода топлива [л / (т * (км/ч)^2 * ч)]
     * q_e = V_топлива / (M_полная * v^2 * t)
     */
    public double calculateSpecificFuelConsumption(double fuelConsumedLiters, double totalGrossWeightTons,
                                                  double speedKmH, double durationHours) {
        if (fuelConsumedLiters <= 0 || totalGrossWeightTons <= 0 || speedKmH <= 0 || durationHours <= 0) {
            return 0.0;
        }
        double denominator = totalGrossWeightTons * Math.pow(speedKmH, 2) * durationHours;
        return fuelConsumedLiters / denominator;
    }

    /**
     * Расчет часовых затрат на обеспечение технической готовности самосвала:
     * Z_тг = a0 + a1 * exp(-a2 * m_ном)  [руб / час]
     */
    public double calculateHourlyReadinessCost(double ratedPayloadTons) {
        if (ratedPayloadTons <= 0) {
            return MiningConstants.APPROX_READY_A0;
        }
        return MiningConstants.APPROX_READY_A0 + 
               MiningConstants.APPROX_READY_A1 * Math.exp(-MiningConstants.APPROX_READY_A2 * ratedPayloadTons);
    }

    /**
     * Расчет часовых эксплуатационных затрат экскаватора:
     * Z_эк = 4500 * ln(m_ковша_т + 1)  [руб / час]
     */
    public double calculateHourlyExcavatorCost(double bucketVolumeM3) {
        double bucketMassTons = bucketVolumeM3 * MiningConstants.ROCK_DENSITY_TONS_PER_M3;
        return MiningConstants.EXCAVATOR_HOURLY_COST_BASE * Math.log(bucketMassTons + 1.0);
    }

    /**
     * Расчет потребного количества автосамосвалов на 1 экскаватор:
     * N_a = (2 * L * m_ковша) / (v * m_ном * t_цикла)
     */
    public double calculateRequiredTrucksCount(double oneWayDistanceKm, double bucketVolumeM3,
                                             double ratedPayloadTons, double averageSpeedKmH) {
        if (ratedPayloadTons <= 0 || averageSpeedKmH <= 0 || oneWayDistanceKm <= 0) {
            return 0.0;
        }
        double bucketMassTons = bucketVolumeM3 * MiningConstants.ROCK_DENSITY_TONS_PER_M3;
        double numerator = 2.0 * oneWayDistanceKm * bucketMassTons;
        double denominator = ratedPayloadTons * averageSpeedKmH * MiningConstants.EXCAVATOR_BUCKET_CYCLE_HOURS;
        return numerator / denominator;
    }

    /**
     * Полный расчет прямых затрат по единичному рейсу (Trip)
     */
    public CostAnalysisResult calculateSingleTripCost(TripRecordDTO trip, double fuelPricePerLiter) {
        double fuelPrice = fuelPricePerLiter > 0 ? fuelPricePerLiter : MiningConstants.DEFAULT_FUEL_COST_RUB;
        
        // 1. Топливо
        double loadedFuel = Math.max(0.0, trip.getFuelAtLoadingLiters() - trip.getFuelAtUnloadingLiters());
        double emptyFuel = Math.max(0.0, trip.getReturnFuelConsumedLiters());
        double totalFuelLiters = loadedFuel + emptyFuel;
        double fuelCostRub = totalFuelLiters * fuelPrice;

        // 2. Длительность рейса (часы)
        double tripDurationHours = 0.5; // базовое приближение при отсутствии меток
        if (trip.getArrivalLoadingTime() != null && trip.getFinishUnloadingTime() != null) {
            long millis = Math.max(1, trip.getFinishUnloadingTime().toEpochMilli() - trip.getArrivalLoadingTime().toEpochMilli());
            tripDurationHours = millis / 3_600_000.0;
        }
        if (trip.getReturnDurationHours() > 0) {
            tripDurationHours += trip.getReturnDurationHours();
        }

        // 3. Затраты на готовность и амортизацию самосвала
        double readinessCostPerHour = calculateHourlyReadinessCost(trip.getTruckPayloadRatedTons());
        double readinessCostRub = readinessCostPerHour * tripDurationHours;

        // 4. Доля затрат экскаватора на погрузку рейса
        double excavatorHourlyCost = calculateHourlyExcavatorCost(trip.getExcavatorBucketVolumeM3());
        double loadingDurationHours = 0.08; // ~5 минут средняя погрузка
        if (trip.getArrivalLoadingTime() != null && trip.getFinishLoadingTime() != null) {
            long loadMillis = Math.max(1, trip.getFinishLoadingTime().toEpochMilli() - trip.getArrivalLoadingTime().toEpochMilli());
            loadingDurationHours = loadMillis / 3_600_000.0;
        }
        double excavatorCostRub = excavatorHourlyCost * loadingDurationHours;

        // 5. Итоговые показатели
        double totalCostRub = fuelCostRub + readinessCostRub + excavatorCostRub;
        double tonKm = trip.getActualWeightTons() * trip.getTripDistanceKm();
        double costPerTonKm = tonKm > 0 ? (totalCostRub / tonKm) : 0.0;
        double costPerTon = trip.getActualWeightTons() > 0 ? (totalCostRub / trip.getActualWeightTons()) : 0.0;

        return CostAnalysisResult.builder()
                .objectCode(trip.getTruckCode())
                .totalTrips(1)
                .totalDistanceKm(trip.getTripDistanceKm())
                .totalTransportedTons(trip.getActualWeightTons())
                .totalTonKilometers(tonKm)
                .fuelConsumedLiters(totalFuelLiters)
                .fuelCostRubles(fuelCostRub)
                .vehicleReadinessCostRubles(readinessCostRub)
                .excavatorOperationCostRubles(excavatorCostRub)
                .totalCostRubles(totalCostRub)
                .costPerTonKm(costPerTonKm)
                .costPerTon(costPerTon)
                .build();
    }

    /**
     * Сводный расчет себестоимости по группе рейсов (за смену/сутки/месяц)
     */
    public CostAnalysisResult aggregateCostAnalysis(String groupCode, Collection<TripRecordDTO> trips, double fuelPricePerLiter) {
        if (trips == null || trips.isEmpty()) {
            return CostAnalysisResult.builder().objectCode(groupCode).build();
        }

        long count = 0;
        double totalDistance = 0.0;
        double totalTons = 0.0;
        double totalTonKm = 0.0;
        double totalFuelLiters = 0.0;
        double totalFuelCost = 0.0;
        double totalReadinessCost = 0.0;
        double totalExcavatorCost = 0.0;
        double totalCost = 0.0;

        for (TripRecordDTO trip : trips) {
            CostAnalysisResult single = calculateSingleTripCost(trip, fuelPricePerLiter);
            count++;
            totalDistance += single.getTotalDistanceKm();
            totalTons += single.getTotalTransportedTons();
            totalTonKm += single.getTotalTonKilometers();
            totalFuelLiters += single.getFuelConsumedLiters();
            totalFuelCost += single.getFuelCostRubles();
            totalReadinessCost += single.getVehicleReadinessCostRubles();
            totalExcavatorCost += single.getExcavatorOperationCostRubles();
            totalCost += single.getTotalCostRubles();
        }

        double costPerTonKm = totalTonKm > 0 ? (totalCost / totalTonKm) : 0.0;
        double costPerTon = totalTons > 0 ? (totalCost / totalTons) : 0.0;

        return CostAnalysisResult.builder()
                .objectCode(groupCode)
                .totalTrips(count)
                .totalDistanceKm(totalDistance)
                .totalTransportedTons(totalTons)
                .totalTonKilometers(totalTonKm)
                .fuelConsumedLiters(totalFuelLiters)
                .fuelCostRubles(totalFuelCost)
                .vehicleReadinessCostRubles(totalReadinessCost)
                .excavatorOperationCostRubles(totalExcavatorCost)
                .totalCostRubles(totalCost)
                .costPerTonKm(costPerTonKm)
                .costPerTon(costPerTon)
                .build();
    }
}
