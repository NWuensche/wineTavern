package winetavern.model.reservation;

import winetavern.model.management.TimeInterval;

import javax.persistence.*;

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
    @ManyToOne(targetEntity=Table.class, cascade = CascadeType.ALL) Table table;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;


    @Deprecated
    protected Reservation(){}

    public Reservation(String guestName, int persons, Table table, TimeInterval interval) {
        this.guestName = guestName;
        this.persons = persons;
        this.table = table;
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

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public void setInterval(TimeInterval interval) {
        this.interval = interval;
    }
}
