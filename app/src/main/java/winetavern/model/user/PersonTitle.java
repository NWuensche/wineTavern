package winetavern.model.user;

/**
 * Enum for the titles that a person can have.
 * @author Niklas Wünsche
 */

public enum PersonTitle {
    MISTER("Herr"),
    MISSES("Frau");

    private String personTitle;

    PersonTitle(String personTitle) {
        this.personTitle = personTitle;
    }

    public String getGerman() {
        return personTitle;
    }
}
