package winetavern.model.management;

import org.salespointframework.time.Interval;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author Louis, Michel
 */

@Entity
public class TimeInterval {
    @Id @GeneratedValue private long id;
    private LocalDateTime start;
    private LocalDateTime end;

    @Deprecated
    protected TimeInterval() {}

    public TimeInterval(LocalDateTime start, LocalDateTime end) {
        setStart(start);
        setEnd(end);
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

    public TimeInterval moveIntervalByMinutes(int minutes) {
        if (minutes < 0) {
            this.start = start.minusMinutes(minutes);
            this.end = end.minusMinutes(minutes);
        } else {
            this.start = start.plusMinutes(minutes);
            this.end = end.plusMinutes(minutes);
        }
        return this;
    }

    public boolean intersects(TimeInterval other) {
        return (timeInInterval(other.getStart()) || timeInInterval(other.getEnd()) ||
                this.getStart().isEqual(other.getStart()) || this.getEnd().isEqual(other.getEnd()));
    }

    public boolean timeInInterval(LocalDateTime time) {
        Interval interval = this.toInterval();
        return (interval.getStart().isBefore(time) &&
                interval.getEnd().isAfter(time));
    }
}
