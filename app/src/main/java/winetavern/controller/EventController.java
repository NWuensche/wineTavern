package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

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
        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
        return "events";
    }

    @RequestMapping("/admin/events/add")
    public String showAddEventTemplate(){
        return "addevent";
    }

    @PostMapping("/admin/events/add")
    public String addEvent(@ModelAttribute String name, @ModelAttribute String desc, @ModelAttribute String date,
                           @ModelAttribute String price) {
        //TODO parse start and end, given in the form: "dd.MM.YYYY HH:mm - dd.MM.YYYY HH:mm"
        LocalDateTime start = LocalDateTime.of(1,1,1,1,1);
        LocalDateTime end = LocalDateTime.of(9,9,9,9,9);
        TimeInterval interval = new TimeInterval(start,end);

        eventCatalog.save(new Event(name, Money.of((Float.valueOf(price)),EURO),interval,desc));
        return "redirect:/admin/events";
    }

    @RequestMapping(value="/admin/events/remove", method = RequestMethod.POST)
    public String removeEvent(@ModelAttribute(value = "event") Event event) {
        eventCatalog.delete(event);
        return "redirect:/admin/events";
    }

    private Set<Event> getEventsByInterval(TimeInterval i1) {
        Set<Event> res = new TreeSet<>();
        for (Event event : eventCatalog.findAll()) {
            TimeInterval i2 = event.getInterval();
            if (i2.getStart().compareTo(i1.getStart()) == 1 || //if a part of the event lies in the interval i1
                    i2.getEnd().compareTo(i1.getEnd()) == -1)
                res.add(event);
        }
        return res;
    }
}
