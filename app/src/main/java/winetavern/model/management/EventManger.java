package winetavern.model.management;

import org.salespointframework.time.Interval;

import java.time.Duration;
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

    /*
    public Set<Event> getEventsByInterval(Interval i1) {
        Set<Event> res = new HashSet<>();
        for (Event event : events) {
            Interval i2 = event.getInterval();
            if (i2.getStart().compareTo(i1.getStart()) == 1 || //if a part of the event lies in the interval i1
                    i2.getEnd().compareTo(i1.getEnd()) == -1)
                res.add(event);
        }
        return res;
    }
    */
}
