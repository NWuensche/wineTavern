package winetavern.model.reservation;

import org.salespointframework.time.Interval;
import winetavern.model.management.TimeInterval;

import javax.persistence.*;
import java.sql.Time;
import java.time.Duration;
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
    @ManyToOne(targetEntity=Table.class, cascade = CascadeType.ALL) Table table;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;


    @Deprecated
    protected Reservation(){}

    public Reservation(String guestName, Table table, TimeInterval interval) {
        this.guestName = guestName;
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
