package winetavern.controller;

import lombok.NonNull;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.External;
import winetavern.model.user.ExternalManager;
import winetavern.model.user.Vintner;
import winetavern.model.user.VintnerManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class EventController {
    @NonNull @Autowired private EventCatalog eventCatalog;
    @NonNull @Autowired private ExternalManager externals;
    @NonNull @Autowired private VintnerManager vintnerManager;

    @RequestMapping("/admin/events")
    public String manageEvents(Model model) {

        //TODO filter for events in the past
        //TODO sort by time, next one first
        Event event = eventCatalog.findAll().iterator().next();
        model.addAttribute("test",event.getPrice().getContext());
        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
        model.addAttribute("calendarString", buildCalendarString());
        return "events";
    }

    @RequestMapping("/admin/events/add")
    public String showAddEventTemplate(Model model){
        model.addAttribute("externals",externals.findAll());
        return "addevent";
    }

    /**
    @param name         the name of the event
    @param desc         the description of the event
    @param date         the start and end time of the event. Format: 'dd.MM.yyyy HH:mm - dd.MM.yyyy HH:mm'
    @param price        the ticket price for the event (what the customer pays)
    @param external     the id of the external who is featured at this event (the one who gets payed)
                        '0' if a new external will be created
    @param externalName optional - the name of the external to create
    @param externalWage optional - the wage of the external to create
     */
    @PostMapping("/admin/events/add")
    public String addEvent(@RequestParam String name, @RequestParam String desc, @RequestParam String date,
                           @RequestParam String price, @RequestParam String external, @RequestParam String externalName,
                           @RequestParam String externalWage) {

        if (!date.isEmpty()) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String[] splittedDate = date.split("\\s-\\s");
            if (splittedDate.length == 2) {
                LocalDateTime start = LocalDateTime.parse(splittedDate[0], parser);
                LocalDateTime end = LocalDateTime.parse(splittedDate[1], parser);

                External externalPerson;
                if(external.equals("0")) { //external == '0' => create new external
                    externalPerson = new External(externalName, Money.of(Float.parseFloat(externalWage), EURO));
                } else { //external already exists
                    externalPerson = externals.findOne(Long.parseLong(external)).get();
                }

                eventCatalog.save(new Event(name, Money.of((Float.parseFloat(price)), EURO),
                                  new TimeInterval(start, end), desc, externalPerson));
            }
        }
        return "redirect:/admin/events";
    }

    @RequestMapping("/admin/events/change/{event}")
    public String showChangeModal(@PathVariable Event event, Model model){
        model.addAttribute("externals",externals.findAll());
        model.addAttribute("event",event);
        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
        model.addAttribute("calendarString", buildCalendarString());
        return "events";
    }

    @PostMapping("/admin/events/change/{event}")
    public String changeEvent(@PathVariable Event event, @RequestParam String name, @RequestParam String desc, @RequestParam String date,
                              @RequestParam String price, @RequestParam String external, @RequestParam String externalName,
                              @RequestParam String externalWage){

        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String[] splittedDate = date.split("\\s-\\s");
        if (splittedDate.length == 2) {
            LocalDateTime start = LocalDateTime.parse(splittedDate[0], parser);
            LocalDateTime end = LocalDateTime.parse(splittedDate[1], parser);

            External externalPerson;
            if (external.equals("0")) { //external == '0' => create new external
                externalPerson = new External(externalName,Money.of(BigDecimal.valueOf(Double.parseDouble(externalWage)),
                        EURO));
            } else { //external already exists
                externalPerson = externals.findOne(Long.parseLong(external)).get();
            }

            event.setName(name);
            event.setDescription(desc);
            event.setInterval(new TimeInterval(start, end));
            event.setPrice(Money.of(Float.parseFloat(price),EURO));
            event.setExternal(externalPerson);
            eventCatalog.save(event);
        }

        return "redirect:/admin/events";
    }

    @RequestMapping("/admin/events/vintner")
    public String showVintner(@RequestParam Optional<String> query, Model model){
        if (query.isPresent()) {
            setVintnerSequence(query.get());
        }

        model.addAttribute("vintners", vintnerManager.findAllByOrderByPosition());
        return "vintner";
    }

    /**
     * splits the query String and saves all vintners in the exact same order by setting the position attribute
     * @see Vintner with attribut (int position), the position in the vintner evening sequence
     * @param query the String which contains all vintners to keep in the system (in that order)
     *              format: vintnerName|vintnerName|...|
     */
    private void setVintnerSequence(String query) {
        String[] vintnerNames = query.split("\\|"); //format: vintnerName|vintnerName|...|
        List<Vintner> vintnersToRemove = vintnerManager.findAll();

        for (int i = 0; i < vintnerNames.length; i++) { //iterate through all vintners in the query
            Optional<Vintner> vintnerOptional = vintnerManager.findByName(vintnerNames[i]);

            if (vintnerOptional.isPresent()) { //the vintner already exists in the DB
                vintnerOptional.get().setPosition(i); //set vintners position in the vintner evening sequence
                vintnerManager.save(vintnerOptional.get());
                vintnersToRemove.remove(vintnerOptional.get());
            } else {
                vintnerManager.save(new Vintner(vintnerNames[i], i));
            }
        }

        vintnersToRemove.forEach(it -> vintnerManager.delete(it)); //delete all unused vintners
    }

    private String buildCalendarString() {
        String calendarString = "[";
        boolean noComma = true;

        for (Event event : eventCatalog.findAll()) {
            if (noComma)
                noComma = false;
            else
                calendarString = calendarString + ",";

            TimeInterval interval = event.getInterval();
            calendarString = calendarString +
                    "{\"title\":\"" + event.getName() +
                    "\",\"start\":\"" + interval.getStart() +
                    "\",\"end\":\"" + interval.getEnd() +
                    "\",\"url\":\"" + "/admin/events/change/" + event.getId() +
                    "\",\"description\":\"" + event.getDescription() + "<br/><br/>" + event.getExternal().getName() +
                    "<br/>" + event.getPrice().getNumber().doubleValue() + "€" + "\"}";
        }

        return calendarString + "]";
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
