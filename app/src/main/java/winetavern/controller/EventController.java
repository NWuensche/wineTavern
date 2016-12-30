package winetavern.controller;

import lombok.NonNull;
import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.Helper;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @NonNull @Autowired private ExternalManager externalManager;
    @NonNull @Autowired private VintnerManager vintnerManager;
    @NonNull @Autowired private PersonManager personManager;
    @NonNull @Autowired private BusinessTime businessTime;

    @RequestMapping("/admin/events")
    public String manageEvents(Model model) {
        checkVintnerDays();
        model.addAttribute("eventAmount", eventCatalog.count());
        model.addAttribute("events", eventCatalog.findAll());
        model.addAttribute("calendarString", buildCalendarString());
        return "events";
    }

    /**
     * automatically creates Events for the vintner days till the present day and the next one
     */
    private void checkVintnerDays() {
        LinkedList<Vintner> vintnerSequence = vintnerManager.findByActiveTrueOrderByPosition(); //active vintners sorted
        if (vintnerSequence.isEmpty())
            return;

        LinkedList<Event> vintnerDays = eventCatalog.findByVintnerDayTrue(); //all vintner days
        Event lastVintnerDay;

        if (vintnerDays.isEmpty()) { //no vintner day yet, begin to save it at Fridays from 2014 on
            lastVintnerDay = createVintnerDay(vintnerSequence.getFirst(), LocalDate.of(2014, 1, 3));
            eventCatalog.save(lastVintnerDay);
            vintnerDays.add(lastVintnerDay);
        } else {
            vintnerDays.sort(Comparator.comparing(o -> o.getInterval().getStart()));
            lastVintnerDay = vintnerDays.getLast(); //get the last vintner day which is actually in the calendar
        }

        while (lastVintnerDay.getInterval().getStart().isBefore(businessTime.getTime())) {
            lastVintnerDay = createVintnerDay(getNextVintner(vintnerSequence, (Vintner) lastVintnerDay.getPerson()),
                    getNextVintnerDayDate(lastVintnerDay.getInterval().getStart().toLocalDate()));

            eventCatalog.save(lastVintnerDay);
        }
    }

    /**
     * returns the next vintner in the given sequence. If it reaches the end, the circle starts from the beginning.
     * WARNING: The last vintner might not be on the sequence anymore. The circle will take its index anyway!
     * @see Vintner
     * @param vintners the sequence
     * @param lastVintner the vin
     * @return Vintner nextVintner - the vintner who is next in the sequence
     */
    private Vintner getNextVintner(List<Vintner> vintners, Vintner lastVintner) {
        return vintners.get((vintners.indexOf(lastVintner) + 1) % vintners.size());
    }

    /**
     * finds the next date to create a vintner day on. Created with the given constant 'dateToCreateVintnerDay'. The
     * next date will be on the same 'DayOfWeek' as the sample date 'dateToCreateVintnerDay'.
     * @param lastDate the LocalDate of the last vintner event
     * @return LocalDate nextDate - the next date (last date plus 2 months) to create a vintner day on
     */
    private LocalDate getNextVintnerDayDate(LocalDate lastDate) {
        LocalDate nextDate = lastDate.plusMonths(2).withDayOfMonth(1).with(lastDate.getDayOfWeek());
        if (nextDate.getMonthValue() % 2 == 0) //date slipped in last month => add one week to get first DayOfWeek in month
            nextDate = nextDate.plusWeeks(1);
        return nextDate;
    }

    /**
     * creates an event with a given vintner as person and a date
     * @see Event
     * @param vintner Vintner extends Person
     * @param date the date which will be converted into LocalDateTime for the interval. The interval will be empty,
     *             because the event is 'allDay' anyway ('allDay' is an calendar attribute).
     * @return Event vintnerDAy - the created event
     */
    private Event createVintnerDay(Vintner vintner, LocalDate date) {
        Event vintnerDay = new Event("Weinprobe: " + vintner, Money.of(0, EURO),
                new TimeInterval(date.atStartOfDay(), date.atStartOfDay()),
                "Weinprobe mit Weinen von " + vintner + ". Alle Weine zum halben Preis!", vintner);
        vintnerDay.setVintnerDay(true);
        return vintnerDay;
    }

    /**
     * compiles all events into a String which can be parsed into an Object by JSON (javascript) and then put into the
     * calendar. Also add virtual events to the future. These are the estimated vine evening events.
     * @return JSON parsable String
     */
    private String buildCalendarString() {
        String calendarString = "[";
        boolean noComma = true;

        Set<Event> events = eventCatalog.findAll(); //all existing events

        LinkedList<Vintner> vintnerSequence = vintnerManager.findByActiveTrueOrderByPosition(); //active vintners sorted
        if (!vintnerSequence.isEmpty()) { //add virtual events

            LinkedList<Event> vintnerDays = eventCatalog.findByVintnerDayTrue(); //all vintner days
            vintnerDays.sort(Comparator.comparing(o -> o.getInterval().getStart()));
            Event lastVintnerDay = vintnerDays.getLast(); //get the last vintner day which is actually in the calendar

            while (lastVintnerDay.getInterval().getStart().isBefore(businessTime.getTime().plusYears(5))) {
                lastVintnerDay = createVintnerDay(getNextVintner(vintnerSequence, (Vintner) lastVintnerDay.getPerson()),
                        getNextVintnerDayDate(lastVintnerDay.getInterval().getStart().toLocalDate()));

                events.add(lastVintnerDay);
            }
        }

        for (Event event : events) { //add all events
            if (noComma)
                noComma = false;
            else
                calendarString = calendarString + ",";

            TimeInterval interval = event.getInterval();
            calendarString = calendarString +
                    "{\"title\":\"" + event.getName() +
                    "\",\"allDay\":" + event.isVintnerDay() +
                    ",\"start\":\"" + interval.getStart() +
                    "\",\"end\":\"" + interval.getEnd();

            if (!event.isVintnerDay()) {
                calendarString = calendarString +
                        "\",\"url\":\"" + "/admin/events/change/" + event.getId();
            }

            calendarString = calendarString +
                    "\",\"description\":\"" + event.getDescription() + "<br/><br/>" + event.getPerson();

            if(event.getPrice().getNumber().doubleValue() == 0)
                calendarString += "<br/>Freier Eintritt!\"}";
            else
                calendarString += "<br/>Eintritt: " + Helper.moneyToEuroString(event.getPrice())+ "\"}";
        }

        return calendarString + "]";
    }

    @RequestMapping("/admin/events/add")
    public String showAddEventTemplate(Model model){
        model.addAttribute("externals", externalManager.findAll());
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

                Person externalPerson;
                if(external.equals("0")) { //external == '0' => create new external
                    externalPerson = new External(externalName, Money.of(Float.parseFloat(externalWage), EURO));
                } else { //external already exists
                    externalPerson = personManager.findOne(Long.parseLong(external)).get();
                }

                //TODO doesnt save
                eventCatalog.save(new Event(name, Money.of((Float.parseFloat(price)), EURO),
                                  new TimeInterval(start, end), desc, externalPerson));
            }
        }
        return "redirect:/admin/events";
    }

    @RequestMapping("/admin/events/change/{event}")
    public String showChangeModal(@PathVariable Event event, Model model){
        model.addAttribute("interval", Helper.localDateTimeToDateTimeString(event.getInterval().getStart())
                           + " - " + Helper.localDateTimeToDateTimeString(event.getInterval().getEnd()));
        model.addAttribute("externals", externalManager.findAll());
        model.addAttribute("event", event);
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

            Person externalPerson;
            if (external.equals("0")) { //external == '0' => create new external
                externalPerson = new External(externalName,Money.of(BigDecimal.valueOf(Double.parseDouble(externalWage)),
                        EURO));
            } else { //external already exists
                externalPerson = personManager.findOne(Long.parseLong(external)).get();
            }

            event.setName(name);
            event.setDescription(desc);
            event.setInterval(new TimeInterval(start, end));
            event.setPrice(Money.of(Float.parseFloat(price),EURO));
            event.setPerson(externalPerson);
            eventCatalog.save(event);
        }

        return "redirect:/admin/events";
    }

    @RequestMapping("/admin/events/remove/{event}")
    public String removeEvent(@PathVariable Event event){
        eventCatalog.delete(event);
        return "redirect:/admin/events";
    }

    @RequestMapping("/admin/events/vintner")
    public String showVintner(@RequestParam Optional<String> query, Model model){
        if (query.isPresent()) {
            setVintnerSequence(query.get());
            return "redirect:/admin/events";
        }
        model.addAttribute("missing",vintnerManager.findByActiveFalse());
        model.addAttribute("vintners", vintnerManager.findByActiveTrueOrderByPosition());
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
        List<Vintner> vintnersToRemove = vintnerManager.findByActiveTrue();

        for (int i = 0; i < vintnerNames.length; i++) { //iterate through all vintners in the query
            Optional<Vintner> vintnerOptional = vintnerManager.findByName(vintnerNames[i]);

            if (vintnerOptional.isPresent()) { //the vintner already exists in the DB
                Vintner vintner = vintnerOptional.get();
                vintner.setPosition(i); //set vintners position in the vintner evening sequence
                vintner.setActive(true);
                vintnerManager.save(vintner);
                vintnersToRemove.remove(vintner);
            } else if(!vintnerNames[i].replace(" ", "").equals("")) {
                vintnerManager.save(new Vintner(vintnerNames[i], i));
            }
        }

        for (Vintner vintnerToRemove : vintnersToRemove) { //hide all unused vintners - keep for events in the past
            vintnerToRemove.setActive(false);
            vintnerManager.save(vintnerToRemove);
        }
    }
}
