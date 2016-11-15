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
        setEnd(end);
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        if (start == null || end == null) throw new NullPointerException("the time stamp(s) must not be null");
        if (start.compareTo(end) == 0) throw new IllegalArgumentException("the start time must not be the end time");
        if (start.compareTo(end) == 1) throw new IllegalArgumentException("the start must be sooner than the end");
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        if (start == null || end == null) throw new NullPointerException("the time stamp(s) must not be null");
        if (start.compareTo(end) == 0) throw new IllegalArgumentException("the start time must not be the end time");
        if (start.compareTo(end) == 1) throw new IllegalArgumentException("the start must be sooner than the end");
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

    public static boolean intersects(TimeInterval first, TimeInterval second){
        return (timeInInterval(first.getStart(), second) || timeInInterval(first.getEnd(), second) ||
                first.getStart().isEqual(second.getStart()) || first.getEnd().isEqual(second.getEnd()));
    }

    public static boolean timeInInterval(LocalDateTime time, TimeInterval interval){
        return (interval.getStart().isBefore(time) && interval.getEnd().isAfter(time));
    }
}
