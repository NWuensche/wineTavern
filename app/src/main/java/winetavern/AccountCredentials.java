package winetavern;

/**
 * Class to store credentials from the login page.
 * @author michel
 */

// TODO Make this an inner class of PersonManagerController
public class AccountCredentials {

    private String personTitle;
    private String firstName;
    private String lastName;
    private String birthday;
    private String username;
    private String password;
    private String role;
    private String address;

    public String getPersonTitle() {
        return personTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }

    public String getAddress() {
        return address;
    }

    public void setPersonTitle(String per) {
        this.personTitle = per;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
