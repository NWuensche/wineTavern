package winetavern.model.user;

import lombok.Getter;
import lombok.Setter;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity for Employees
 * Adds information to UserAccount
 * @author Niklas Wünsche
 * @implNote The UserAccount has exactly 1 Role
 */

@Entity
public class Employee extends Person {

    @Id @GeneratedValue @Getter private Long id;
    @OneToOne @Getter private UserAccount userAccount;

    @Getter @Setter private String address;
    @Getter private LocalDate birthday;
    private String personTitle;

    @Deprecated
    protected Employee() {}

    /**
     * @param userAccount needs to have exactly 1 role
     * @param address can be null
     * @param birthday can be null
     * @throws IllegalArgumentException if userAccount has not exactly 1 Role
     */
    public Employee(UserAccount userAccount, String address, String birthday, String personTitle)
            throws IllegalArgumentException {
        if(numberOfRoles(userAccount) != 1) {
            throw new IllegalArgumentException("The UserAccount should have exactly 1 Role!");
        }

        this.userAccount = userAccount;
        this.address = address;
        this.birthday = parseBirthday(birthday);
        this.personTitle = personTitle;
    }

    private LocalDate parseBirthday(String birthday) {
        if(birthday == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.parse(birthday, formatter);

        return localDate;
    }

    private int numberOfRoles(UserAccount userAccount) {
        return userAccount.getRoles().stream().collect(Collectors.toList()).size();
    }

    public String getDisplayNameOfRole() {
        List<Role> roles = userAccount.getRoles().stream().collect(Collectors.toList());
        Role role = roles.get(0);
        return Roles.getGermanNameOfRole(role);
    }

    public boolean isEnabled() {
        return userAccount.isEnabled();
    }

    public String getPersonTitle() {
        return personTitle;
    }

    public Role getRole() {
        return userAccount.getRoles().stream().findFirst().get();
    }

    @Override
    public String toString() {
        return userAccount.getFirstname() + " " + userAccount.getLastname();
    }
}
