package winetavern.model.user;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import winetavern.model.DateParameter;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity for Persons
 * Adds information to UserAccount
 * @author Niklas Wünsche
 * @implNote The UserAccount has exactly 1 Role
 */

@Entity
public class Person {

    @Id @GeneratedValue private long id;
    @OneToOne private UserAccount userAccount;

    private String address;
    private Calendar birthday;

    @Deprecated
    protected Person() {}

    /**
     * @param userAccount needs to have exactly 1 role
     * @param address can be null
     * @param birthday can be null
     * @throws IllegalArgumentException if userAccount has not exactly 1 Role
     */
    public Person(UserAccount userAccount, String address, DateParameter birthday) throws IllegalArgumentException {

        if(numberOfRoles(userAccount) != 1) {
            throw new IllegalArgumentException("The UserAccount should have exactly 1 Role!");
        }

        this.userAccount = userAccount;
        this.address = address;
        this.birthday = (birthday != null) ? birthday.getCalendar() : null;

    }

    private int numberOfRoles(UserAccount userAccount) {
        return userAccount.getRoles().stream().collect(Collectors.toList()).size();
    }

    public long getId() {
        return id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public Optional<Calendar> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public String getDisplayNameOfRole() {
        List<Role> roles = userAccount.getRoles().stream().collect(Collectors.toList());
        Role role = roles.get(0);
        return Roles.getDisplayNameRole(role);
    }

    @Override
    public String toString() {
        return userAccount.getFirstname() + " " + userAccount.getLastname();
    }
}
