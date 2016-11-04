package winetavern.model.management;

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

    public Set<Event> getEventsByTime(LocalDateTime time) {
        Set<Event> res = new HashSet<>();
        return res;
    }
}
