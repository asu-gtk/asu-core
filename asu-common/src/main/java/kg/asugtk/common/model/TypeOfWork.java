package kg.asugtk.common.model;

public enum TypeOfWork {
    ORE("Добыча руды"),
    WASTE("Вскрыша"),
    OVERBURDEN("Переэкскавация"),
    TRANSIT("Транзитная перевозка");

    private final String description;

    TypeOfWork(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
