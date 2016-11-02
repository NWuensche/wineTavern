package kickstart;

/**
 * Class to store credentials from the login page.
 * @author michel
 */

public class AccountCredentials {

    private String username;
    private String password;
    private String role = "";

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
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
}
