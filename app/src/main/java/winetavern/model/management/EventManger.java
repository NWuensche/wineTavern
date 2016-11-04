package winetavern.model.management;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class EventManger {
    private static EventManger instance;
    private Set<Event> events;

    public EventManger() {
        this.events = new HashSet<>();
    }

    public static EventManger getInstance() {
        if (EventManger.instance == null)
            EventManger.instance = new EventManger();
        return EventManger.instance;
    }

    public boolean addEvent(Event event) {
        return events.add(event);
    }

    public boolean removeEvent(Event event) {
        return events.remove(event);
    }

    public Set<Event> getEventsByDate(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        Set<Event> res = new HashSet<>();
        for (Event event : events)
            if (event.getDate().toLocalDate().equals(date)) res.add(event);
        return res;
    }

    public LocalDateTime addDuration(LocalDateTime time, Duration duration) {
        return time.plusDays(duration.toDays())
                   .plusHours(duration.toHours())
                   .plusMinutes(duration.toMinutes())
                   .plusSeconds(duration.toMillis() / 1000)
                   .plusNanos(duration.toNanos());
    }
}
