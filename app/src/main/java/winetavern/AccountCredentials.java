package winetavern;

import lombok.Getter;
import lombok.Setter;

/**
 * Class to store credentials from the login page.
 * @author michel
 */
@Getter
@Setter
public class AccountCredentials {

    private String personTitle;
    private String firstName;
    private String lastName;
    private String birthday;
    private String username;
    private String password;
    private String role;
    private String address;

}
