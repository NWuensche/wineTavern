package winetavern.model.stock;

import org.salespointframework.quantity.Metric;

/**
 * Enum of all categories
 * @author Niklas Wünsche
 */

public enum Category {

    RED_WINE("Rotwein", Metric.LITER),
    WHITE_WINE("Weißwein", Metric.LITER),
    SOFT_DRINK("Alkoholfreie Getränke", Metric.LITER),
    LIQUOR("Spirituosen", Metric.LITER),

    SNACK("Snacks", Metric.UNIT),
    MENU("Menüs", Metric.UNIT);

    private String categoryName;
    private Metric metric;

    Category(String categoryName, Metric metric) {
        this.categoryName = categoryName;
        this.metric = metric;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Metric getMetric() {
        return metric;
    }

    @Override
    public String toString() {
        return categoryName;
    }

}
