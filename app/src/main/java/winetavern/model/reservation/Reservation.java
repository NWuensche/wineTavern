package winetavern.model.reservation;

import winetavern.model.management.TimeInterval;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for a single reservation
 * @author Sev
 */
@Entity
public class Reservation {

    @Id @GeneratedValue
    private long id;

    private String guestName;
    private int persons;
    @ManyToOne(targetEntity=Desk.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "desk_id")
    private Desk desk;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;


    @Deprecated
    protected Reservation(){}

    public Reservation(String guestName, int persons, Desk desk, TimeInterval interval) {
        this.guestName = guestName;
        this.persons = persons;
        this.setDesk(desk);
        this.interval = interval;
    }

    public long getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public Desk getDesk() {
        return desk;
    }

    public void setDesk(Desk desk) {
        this.desk = desk;
        desk.addReservation(this);
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public void setInterval(TimeInterval interval) {
        this.interval = interval;
    }

    public LocalDateTime getReservationStart() {
        return interval.getStart();
    }
}
