package winetavern.model.user;

/**
 * Enum for the titles that a person can have.
 * @author Niklas Wünsche
 */

public enum PersonTitle {

    MISTER("Herr"),
    MISSES("Frau");

    private String germanTitle;

    PersonTitle(String germanTitle) {
        this.germanTitle = germanTitle;
    }

    public String getGerman() {
        return germanTitle;
    }

}
