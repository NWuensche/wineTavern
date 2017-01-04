package winetavern.model.management;

import lombok.Getter;
import lombok.NonNull;
import org.salespointframework.time.Interval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author Louis
 */
// TODO Should this really be an entity? We do not even have a Repository
@Entity
public class TimeInterval {
    @Getter @Id @GeneratedValue private long id;
    @Getter @NonNull private LocalDateTime start;
    @Getter @NonNull private LocalDateTime end;

    @Deprecated protected TimeInterval() {}

    public TimeInterval(LocalDateTime start, LocalDateTime end) {
        if(end.isBefore(start))
            throw new IllegalArgumentException("End should not be before start");
        this.start = start;
        this.end = end;

    }

    public void setStart(LocalDateTime start) {
        if(end.isBefore(start))
            throw new IllegalArgumentException("End should not be before start");
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        if(end.isBefore(start))
            throw new IllegalArgumentException("End should not be before start");
        this.end = end;
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }

    public Interval toInterval() {
        return Interval.from(start).to(end);
    }

    // TODO: Why move the called TimeInterval, but also return the object itself?
    public TimeInterval moveIntervalByMinutes(int minutes) {
        this.start = start.plusMinutes(minutes);
        this.end = end.plusMinutes(minutes);
        return this;
    }

    public TimeInterval returnMovedIntervalByMinutes(int minutes) {
        return new TimeInterval(start.plusMinutes(minutes), end.plusMinutes(minutes));
    }

    public boolean intersects(TimeInterval other) {
        return timeInInterval(other.getStart()) || timeInInterval(other.getEnd()) ||
               other.timeInInterval(start) || other.timeInInterval(end);
    }

    public boolean timeInInterval(LocalDateTime time) {
        return (this.start.isBefore(time) && this.end.isAfter(time)) || this.start.isEqual(time) || this.end.isEqual(time);
    }
}
