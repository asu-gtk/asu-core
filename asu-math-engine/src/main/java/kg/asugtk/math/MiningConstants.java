package kg.asugtk.math;

public final class MiningConstants {

    private MiningConstants() {}

    /**
     * Плановая длительность суточного фонда чистого рабочего времени, часов (20 ч)
     */
    public static final double DEFAULT_SHIFT_HOURS = 20.0;

    /**
     * Базовая стоимость дизельного топлива, руб/л
     */
    public static final double DEFAULT_FUEL_COST_RUB = 50.0;

    /**
     * Коэффициент перевода номинальной грузоподъемности в массу порожнего самосвала (тара ~ 0.73-0.82)
     */
    public static final double TRUCK_TARE_RATIO = 0.73;

    /**
     * Коэффициенты аппроксимации затрат на техническую готовность самосвалов Z_тг = a0 + a1 * exp(-a2 * m_nom)
     */
    public static final double APPROX_READY_A0 = 4000.0;
    public static final double APPROX_READY_A1 = 7245.0;
    public static final double APPROX_READY_A2 = 0.0145;

    /**
     * Коэффициент нормативной загрузки кузова
     */
    public static final double OPTIMAL_LOAD_COEFF = 0.90;

    /**
     * Средняя длительность цикла черпания ковша экскаватора, часов (~34.6 секунды)
     */
    public static final double EXCAVATOR_BUCKET_CYCLE_HOURS = 0.00963;

    /**
     * Коэффициент часовых эксплуатационных затрат экскаватора: 4500 * ln(m_ковша + 1)
     */
    public static final double EXCAVATOR_HOURLY_COST_BASE = 4500.0;

    /**
     * Плотность разрыхленной горной массы (т / м3)
     */
    public static final double ROCK_DENSITY_TONS_PER_M3 = 1.8;
}
