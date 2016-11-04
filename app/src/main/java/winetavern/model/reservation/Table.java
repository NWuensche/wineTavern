package winetavern.model.reservation;

import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Sev
 */
@Entity(name = "restaurant_table")
public class Table {

    private @Id long id;

    private int capacity;
    private int number;


    public Table(int capacity,int number) throws InvalidArgumentException{
        if(capacity <= 0){throw new InvalidArgumentException(new String[]{"No Table should have capacity <= 0"});}
        if(number <= 0){throw new InvalidArgumentException(new String[]{"No Table should be identified by number <= " +
                "0"});}
        this.number = number;
        this.capacity = capacity;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
