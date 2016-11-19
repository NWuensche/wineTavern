package winetavern.model.user;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Id @GeneratedValue private Long id;
    @OneToOne private UserAccount userAccount;

    private String address;
    private LocalDate birthday;
    private String personTitle;

    @Deprecated
    protected Person() {}

    /**
     * @param userAccount needs to have exactly 1 role
     * @param address can be null
     * @param birthday can be null
     * @throws IllegalArgumentException if userAccount has not exactly 1 Role
     */
    public Person(UserAccount userAccount, String address, String birthday, String personTitle) throws IllegalArgumentException {
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

    public Long getId() {
        return id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public Optional<LocalDate> getBirthday() {
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

    public boolean isEnabled() {
        return userAccount.isEnabled();
    }

    public String getPersonTitle() {
        return personTitle;
    }

}
