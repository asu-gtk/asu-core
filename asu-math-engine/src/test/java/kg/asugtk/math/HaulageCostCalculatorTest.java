package kg.asugtk.math;

import kg.asugtk.common.dto.CostAnalysisResult;
import kg.asugtk.common.dto.OptimizationRequestDTO;
import kg.asugtk.common.dto.OptimizationResultDTO;
import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.common.model.TypeOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaulageCostCalculatorTest {

    private HaulageCostCalculator calculator;
    private SimplexFleetOptimizer optimizer;

    @BeforeEach
    void setUp() {
        calculator = new HaulageCostCalculator();
        optimizer = new SimplexFleetOptimizer(calculator);
    }

    @Test
    @DisplayName("Проверка расчета затрат на техническую готовность самосвала Z_тг")
    void testHourlyReadinessCost() {
        // БелАЗ 130 тонн
        double cost130 = calculator.calculateHourlyReadinessCost(130.0);
        // a0=4000, a1=7245, a2=0.0145
        // 4000 + 7245 * exp(-0.0145 * 130) = 4000 + 7245 * 0.151829 = 5100.00
        assertThat(cost130).isCloseTo(5100.0, within(10.0));

        // БелАЗ 45 тонн
        double cost45 = calculator.calculateHourlyReadinessCost(45.0);
        assertThat(cost45).isGreaterThan(cost130);
    }

    @Test
    @DisplayName("Проверка расчета часовых затрат экскаватора Z_эк")
    void testHourlyExcavatorCost() {
        // ЭКГ-5А (V=5 м3 -> масса ковша = 5 * 1.8 = 9 т)
        double costEkg5 = calculator.calculateHourlyExcavatorCost(5.0);
        // 4500 * ln(9 + 1) = 4500 * ln(10) = 4500 * 2.302585 = 10361.63 руб/ч
        assertThat(costEkg5).isCloseTo(10361.6, within(5.0));
    }

    @Test
    @DisplayName("Проверка расчета потребного числа самосвалов N_a")
    void testRequiredTrucksCount() {
        // Дистанция L = 3.0 км, Ковш V = 5.0 м3 (9 т), Самосвал 130 т, Скорость 25 км/ч
        // N_a = (2 * 3.0 * 9.0) / (130 * 25 * 0.00963) = 54 / 31.2975 = 1.725 самосвала
        double count = calculator.calculateRequiredTrucksCount(3.0, 5.0, 130.0, 25.0);
        assertThat(count).isCloseTo(1.725, within(0.01));
    }

    @Test
    @DisplayName("Проверка сквозного расчета себестоимости одного рейса")
    void testSingleTripCost() {
        Instant now = Instant.now();
        TripRecordDTO trip = TripRecordDTO.builder()
                .tripId("TRIP-001")
                .truckCode("TRUCK-101")
                .truckModel("БелАЗ-75131")
                .truckPayloadRatedTons(130.0)
                .truckWeightEmptyTons(107.0)
                .excavatorCode("EXC-02")
                .excavatorBucketVolumeM3(5.0)
                .typeOfWork(TypeOfWork.ORE)
                .tripDistanceKm(3.5)
                .actualWeightTons(128.0)
                .fuelAtLoadingLiters(450.0)
                .fuelAtUnloadingLiters(435.0) // 15 литров расход груженым
                .returnFuelConsumedLiters(10.0) // 10 литров порожним
                .arrivalLoadingTime(now)
                .finishLoadingTime(now.plusSeconds(300))
                .beginUnloadingTime(now.plusSeconds(1200))
                .finishUnloadingTime(now.plusSeconds(1300))
                .returnDurationHours(0.2)
                .build();

        CostAnalysisResult result = calculator.calculateSingleTripCost(trip, 50.0);

        assertThat(result.getFuelConsumedLiters()).isEqualTo(25.0);
        assertThat(result.getFuelCostRubles()).isEqualTo(1250.0); // 25 л * 50 руб
        assertThat(result.getTotalTransportedTons()).isEqualTo(128.0);
        assertThat(result.getTotalDistanceKm()).isEqualTo(3.5);
        assertThat(result.getTotalTonKilometers()).isEqualTo(128.0 * 3.5); // 448 т*км

        // Проверяем, что себестоимость 1 т*км находится в адекватном отраслевом диапазоне (5 - 30 руб / т*км)
        assertThat(result.getCostPerTonKm()).isBetween(5.0, 30.0);
    }

    @Test
    @DisplayName("Проверка модуля планирования и оптимизации смены")
    void testOptimizer() {
        OptimizationRequestDTO request = OptimizationRequestDTO.builder()
                .shiftDurationHours(12.0)
                .excavators(List.of(
                        OptimizationRequestDTO.ExcavatorTargetDTO.builder()
                                .excavatorCode("EXC-02")
                                .bucketVolumeM3(5.0)
                                .planVolumeTons(5000.0)
                                .build()
                ))
                .truckPools(List.of(
                        OptimizationRequestDTO.TruckPoolDTO.builder()
                                .truckModel("БелАЗ-75131")
                                .availableCount(10)
                                .ratedPayloadTons(130.0)
                                .averageSpeedKmH(25.0)
                                .build()
                ))
                .routes(List.of(
                        OptimizationRequestDTO.RouteDistanceDTO.builder()
                                .excavatorCode("EXC-02")
                                .unloadPointCode("DUMP-01")
                                .oneWayDistanceKm(3.0)
                                .build()
                ))
                .build();

        OptimizationResultDTO result = optimizer.optimizeShiftAllocation(request);

        assertThat(result.isOptimalSolutionFound()).isTrue();
        assertThat(result.getAllocations()).isNotEmpty();
        assertThat(result.getAllocations().get(0).getIntegerAssignedTrucks()).isGreaterThanOrEqualTo(1);
    }
}
