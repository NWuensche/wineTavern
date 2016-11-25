package winetavern.model.stock;

/**
 * Enum of all categories
 * @author Niklas Wünsche
 */

public enum Category {

    RED_WINE("RED_WINE"),
    WHITE_WINE("WHITE_WINE"),
    SOFT_DRINK("SOFT_DRINK"),
    LIQUOR("LIQUOR"),

    SNACK("SNACK"),
    MENU("MENU");

    private final String categoryName;

    Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @throws  IllegalArgumentException
     * @return not null
     * @implNote Objects are singular, but the display Names for the menu card are plural.
     */
    public static String getDisplayNameCategory(Category category) {
        return getDisplayNameCategory(category.getCategoryName());
    }

    public static String getDisplayNameCategory(String categoryName) {
        switch(categoryName) {
            case "RED_WINE":
                return "Rotwein";
            case "WHITE_WINE":
                return "Weißwein";
            case "SOFT_DRINK":
                return "Alkoholfreie Getränke";
            case "LIQUOR":
                return "Spirituosen";
            case "SNACK":
                return "Snacks";
            case "MENU":
                return "Menüs";
            default:
                throw new IllegalArgumentException();
        }
    }

}
