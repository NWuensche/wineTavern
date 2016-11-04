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
    @OneToOne UserAccount userAccount;
    private Address address;

    public Person(UserAccount userAccount, Address address) {
        this.userAccount = userAccount;
        this.address = address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

}
