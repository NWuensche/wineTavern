package winetavern.model.user;

/**
 * Created by nwuensche on 18.11.16.
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
