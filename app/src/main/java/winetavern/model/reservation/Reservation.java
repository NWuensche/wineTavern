package winetavern.model.reservation;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author Sev
 */
public class Reservation {
    private int id;
    private String guestName;
    private Table table;
    private LocalDateTime date;
    private Duration duration;

    public Reservation(int id, String guestName, Table table, LocalDateTime date, Duration duration) {
        this.id = id;
        this.guestName = guestName;
        this.table = table;
        this.date = date;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
