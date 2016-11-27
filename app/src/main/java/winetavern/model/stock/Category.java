package winetavern.model.stock;

/**
 * Enum of all categories
 * @author Niklas Wünsche
 */

public enum Category {

    RED_WINE("Rotwein"),
    WHITE_WINE("Weißwein"),
    SOFT_DRINK("Alkoholfreie Getränke"),
    LIQUOR("Spirituosen"),

    SNACK("Snacks"),
    MENU("Menüs");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }

}
