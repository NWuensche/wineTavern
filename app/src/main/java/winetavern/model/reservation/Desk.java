package winetavern.model.reservation;

import javax.persistence.*;
import java.util.List;

/**
 * @author Sev
 */

@Entity
public class Desk {

    @Id @GeneratedValue private long id;
    @OneToMany(targetEntity=Reservation.class, mappedBy="desk") List<Reservation> reservationList;

    private int capacity;
    private String name;

    @Deprecated
    protected Desk(){}

    public Desk(String name, int capacity) throws IllegalArgumentException {

        if(capacity <= 0) {
            throw new IllegalArgumentException ("No Desk should have capacity <= 0");
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
