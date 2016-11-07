package winetavern.model.management;

import org.salespointframework.time.Interval;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author Louis
 */

@Entity
public class TimeInterval {
    @Id @GeneratedValue private long id;
    private LocalDateTime start;
    private LocalDateTime end;

    @Deprecated
    protected TimeInterval() {}

    public TimeInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }

    public Interval toInterval() {
        return Interval.from(start).to(end);
    }
}
