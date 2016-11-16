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
    @ManyToMany(targetEntity=Reservation.class, mappedBy="table") List<Reservation> reservationList;

    private int capacity;
    private String name;

    @Deprecated
    protected Table(){}

    public Table(String name, int capacity) throws IllegalArgumentException {

        if(capacity <= 0) {
            throw new IllegalArgumentException ("No Table should have capacity <= 0");
        }

        this.name = name;
        this.capacity = capacity;
    }

    public  long getId() {
        return id;
    }

    public String getNumber() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Reservation> getReservationList() {
        return reservationList;
    }
}
