package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */

@Controller
public class EventController {
    private final EventCatalog eventCatalog;

    @Autowired
    public EventController(EventCatalog eventCatalog) {
        this.eventCatalog = eventCatalog;
    }

    @RequestMapping("/admin/events")
    public String manageEvents() {
        eventCatalog.findAll();
        return "events";
    }

    @RequestMapping(value="/admin/events/add", method = RequestMethod.POST)
    public String addEvent(@ModelAttribute(value = "event") Event event) {
        eventCatalog.save(event);
        return "redirect:/admin/events";
    }

    @RequestMapping(value="/admin/events/remove", method = RequestMethod.POST)
    public String removeEvent(@ModelAttribute(value = "event") Event event) {
        eventCatalog.delete(event);
        return "redirect:/admin/events";
    }

    public Set<Event> getEventsByInterval(TimeInterval i1) {
        Set<Event> res = new HashSet<>();
        for (Event event : eventCatalog.findAll()) {
            TimeInterval i2 = event.getInterval();
            if (i2.getStart().compareTo(i1.getStart()) == 1 || //if a part of the event lies in the interval i1
                    i2.getEnd().compareTo(i1.getEnd()) == -1)
                res.add(event);
        }
        return res;
    }
}
