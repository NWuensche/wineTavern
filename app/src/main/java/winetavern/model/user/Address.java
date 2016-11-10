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

    /**
     * @implNote If a param is null, it will be converted to an empty string
     */
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

    /**
     * @return never null
     */
    public String getStreet() {
        return street;
    }

    /**
     * @return never null
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return never null
     */
    public String getPostal() {
        return postal;
    }

    public String getCity() {
        return city;
    }

    /**
     * @return never null
     */
    @Override
    public String toString() {
        return street.concat(number).concat(postal).concat(city);
    }

}
