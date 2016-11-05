package winetavern.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Niklas Wünsche
 */

@Entity
public class Address {

    @Id @GeneratedValue private long id;

    private String street;
    private String number;
    private String postal;
    private String city;

    public Address(String street, String number, String postal, String city) {
        this.street = street;
        this.number = number;
        this.postal = postal;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getPostal() {
        return postal;
    }

    public String getCity() {
        return city;
    }

    public void setAddress(String street, String number, String postal, String city) {
        this.street = street;
        this.number = number;
        this.postal = postal;
        this.city = city;
    }

}
