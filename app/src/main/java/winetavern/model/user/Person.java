package winetavern.model.user;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Entity for Persons
 * Adds information to UserAccount
 * @author Niklas Wünsche
 */

@Entity
public class Person {

    @Id @GeneratedValue private long id;
    @OneToOne private UserAccount userAccount;

    private Address address;
    private String birthday;

    public Person(UserAccount userAccount, Address address) {
        this.userAccount = userAccount;
        this.address = address;
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

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

}
