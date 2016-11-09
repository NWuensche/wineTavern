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
        this.street = convertToNotNull(street);
        this.number = convertToNotNull(number);
        this.postal = convertToNotNull(postal);
        this.city   = convertToNotNull(city);
    }

    private String convertToNotNull(String text) {
        String convertedText = text != null ? text : "";
        return convertedText;
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

    @Override
    public String toString() {
        return street.concat(number).concat(postal).concat(city);
    }

}
