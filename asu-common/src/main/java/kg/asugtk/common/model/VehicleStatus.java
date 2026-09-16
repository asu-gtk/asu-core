package kg.asugtk.common.model;

public enum VehicleStatus {
    IDLE("Ожидание / Простой"),
    LOADING("Под погрузкой"),
    HAULING_LOADED("Движение с грузом"),
    UNLOADING("Разгрузка"),
    RETURNING_EMPTY("Возврат порожним"),
    REFUELING("Заправка"),
    MAINTENANCE("Техническое обслуживание"),
    BREAKDOWN("Авария / Ремонт");

    private final String title;

    VehicleStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
