package winetavern.model.user;

import lombok.*;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
@Getter
public class Employee extends Person {

    @Id @GeneratedValue private Long id;
    @OneToOne private UserAccount userAccount;

    @Setter private String address;
    private LocalDate birthday;
    private String personTitle;

    /**
     * @param userAccount needs to have exactly 1 role
     * @throws IllegalArgumentException if userAccount has not exactly 1 Role
     */
    public Employee(UserAccount userAccount, @NonNull String address, @NonNull String birthday, String personTitle)
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

    public Role getRole() {
        return userAccount.getRoles().stream().findFirst().get();
    }

    @Override
    public String toString() {
        return userAccount.getFirstname() + " " + userAccount.getLastname();
    }
}
