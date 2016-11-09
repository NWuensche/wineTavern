package winetavern.model.user;

import org.salespointframework.useraccount.UserAccount;
import winetavern.model.DateParameter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Entity for Persons
 * Adds information to UserAccount
 * @author Niklas Wünsche
 */

@Entity
public class Person {

    @Id @GeneratedValue private long id;
    @OneToOne private UserAccount userAccount;
    @ManyToOne(cascade = CascadeType.ALL) private Address address;

    private Calendar birthday;

    public Person(UserAccount userAccount, Address address, DateParameter birthday) {
        this.userAccount = userAccount;
        this.address = address;
        this.birthday = birthday.getCalendar();
    }

    public long getId() {
        return id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

}
