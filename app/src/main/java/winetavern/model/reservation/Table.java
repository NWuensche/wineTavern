package winetavern.model.reservation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * @author Sev
 */

@Entity(name = "restaurant_table")
public class Table {

    @Id @GeneratedValue private long id;
    @ManyToMany(targetEntity=Reservation.class) List<Reservation> reservationList;

    private int capacity;
    private int number;

    @Deprecated
    protected Table(){}

    public Table(int number, int capacity) throws IllegalArgumentException {

        if(capacity <= 0) {
            throw new IllegalArgumentException ("No Table should have capacity <= 0");
        }

        if(number <= 0) {
            throw new IllegalArgumentException ("No Table should be identified by number <= 0");
        }

        this.number = number;
        this.capacity = capacity;
    }

    public  long getId() {
        return id;
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
