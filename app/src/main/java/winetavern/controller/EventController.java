package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.External;
import winetavern.model.user.ExternalManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class EventController {
    private final EventCatalog eventCatalog;
    private final ExternalManager externals;
    private final BusinessTime time;

    @Autowired
    public EventController(EventCatalog eventCatalog, ExternalManager externals, BusinessTime time) {
        this.eventCatalog = eventCatalog;
        this.externals = externals;
        this.time = time;
    }

    @RequestMapping("/admin/events")
    public String manageEvents(Model model) {
        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
        return "events";
    }

    @RequestMapping("/admin/events/add")
    public String showAddEventTemplate(Model model){
        model.addAttribute("externals",externals.findAll());
        return "addevent";
    }

    @PostMapping("/admin/events/add")
    public String addEvent(@RequestParam String name, @RequestParam String desc, @RequestParam String date,
                           @RequestParam String price, @RequestParam String person, @RequestParam String personname,
                           @RequestParam String
                                       personwage) {
                                                         //TODO stimmt das alles so?
        if (!date.isEmpty()) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String[] splittedDate = date.split("\\s-\\s");
            if (splittedDate.length == 2) {
                LocalDateTime start = LocalDateTime.parse(splittedDate[0], parser);
                LocalDateTime end = LocalDateTime.parse(splittedDate[1], parser);

                External externalPerson;
                if(person.equals("0")){
                    externalPerson = new External(personname,Money.of(Float.valueOf(personwage),EURO)); // das mit
                    // dem float geht nicht wieso auch immer?!

                } else {
                    externalPerson = externals.findByName(person).get();
                }


                eventCatalog.save(new Event(name, Money.of((Float.valueOf(price)), EURO),
                                  new TimeInterval(start, end), desc, externalPerson));
            }
        }
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
