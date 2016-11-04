package winetavern.model.user;

/**
 * Standard Interface for Persons
 * @author Niklas Wünsche
 */

public abstract class Person {

    private String firstName;
    private String lastName;
    private long id;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
