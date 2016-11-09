package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class EventController {
    private final EventCatalog eventCatalog;
    private final BusinessTime time;

    @Autowired
    public EventController(EventCatalog eventCatalog, BusinessTime time) {
        this.eventCatalog = eventCatalog;
        this.time = time;
    }

    @RequestMapping("/admin/events")
    public String manageEvents(Model model) {
        //FOR TESTING ONLY
        if (eventCatalog.count() < 2) {
            eventCatalog.save(new Event("Go hard or go home - Ü80 Party", Money.of(7, EURO),
                    new TimeInterval(LocalDateTime.of(2016, 9, 11, 21, 30), LocalDateTime.of(2016, 9, 11, 23, 30)),
                    "SW4G ist ein muss!"));
            eventCatalog.save(new Event("Grillabend mit Musik von Barny dem Barden", Money.of(7, EURO),
                    new TimeInterval(LocalDateTime.of(2016, 11, 11, 19, 30), LocalDateTime.of(2016, 11, 11, 23, 30)),
                    "Es wird gegrillt und überteuerter Wein verkauft."));
        }
        //END

        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
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
