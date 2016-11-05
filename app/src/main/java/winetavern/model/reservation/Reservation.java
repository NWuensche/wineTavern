package winetavern.model.reservation;

import javax.persistence.*;
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
    @ManyToOne(targetEntity=Table.class)
    Table table;
    private LocalDateTime time;
    private Duration duration;

    public Reservation(String guestName, Table table, LocalDateTime time, Duration duration) {
        this.guestName = guestName;
        this.table = table;
        this.time = time;
        this.duration = duration;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime date) {
        this.time = time;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
