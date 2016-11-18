package winetavern.controller;

import org.salespointframework.catalog.Product;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.management.TimeInterval;
import winetavern.model.menu.DayMenu;
import winetavern.model.reservation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sev, Michel Kunkler
 */
@Controller
public class ReservationManager {

    @Autowired TableRepository tables;
    @Autowired ReservationRepository reservations;
    @Autowired BusinessTime businessTime;

    /**
     * Custom Initbinder makes LocalDateTime working with javascript
     */
    @InitBinder
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, "reservationtime", new LocalDateTimeEditor());
    }

    public class LocalDateTimeEditor extends PropertyEditorSupport {

        // Converts a String to a LocalDateTime (when submitting form)
        @Override
        public void setAsText(String text) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);
            this.setValue(localDateTime);
        }

        // Converts a LocalDateTime to a String (when displaying form)
        @Override
        public String getAsText() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String time = ((LocalDateTime)getValue()).format(formatter);
            return time;
        }

    }

    /**
     * Creates a List containing all reserved Tables at the givven time. Other tables can be assumed free.
     * ToDo: Currently iterating through nearly all reservations. Make MySQL do the job.
     */
    public List<Table> getReservatedTablesByTime(LocalDateTime localDateTime) {
        Iterable<Table> allTables = tables.findAll();
        List<Table> reservedTables = new ArrayList<Table>();
        for( Iterator<Table> tableIterator = allTables.iterator(); tableIterator.hasNext(); ) {
            Table currentTable = tableIterator.next();
            List<Reservation> reservationsOnCurrentTable = currentTable.getReservationList();


            for( Iterator<Reservation> reservationsIterator = reservationsOnCurrentTable.iterator();
                    reservationsIterator.hasNext(); ) {
                Reservation currentReservation = reservationsIterator.next();
                if( isActive(currentReservation, localDateTime) ) {
                    reservedTables.add(currentTable);
                    break;
                }
            }
        }
        return reservedTables;
    }

    /**
     * makes thymeleaf working with the table object by removing everyhting but the name :)
     */
    public List<String> tableToName(List<Table> tableList) {
        List<String> nameList = new ArrayList<String>();
        for( Iterator<Table> tableIterator = tableList.iterator(); tableIterator.hasNext(); ) {
            Table currentTable = tableIterator.next();
            nameList.add(currentTable.getNumber());
        }
        return nameList;
    }

    /**
     * returns true if the reservation is active in the next 2 hours.
     */
    public boolean isActive(Reservation reservation, LocalDateTime localDateTime) {
        TimeInterval timeInterval = new TimeInterval(localDateTime, localDateTime.plusHours(2));
        return timeInterval.intersects(reservation.getInterval());
    }

    public LocalDateTime parseTime(String time) {
        LocalDateTime localDateTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            localDateTime = LocalDateTime.parse(time, formatter);

        } catch (Exception e)  {
            String newTime = time.replace("T", " ").replaceAll(":[0-9][0-9]\\.(.*)", "");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            localDateTime = LocalDateTime.parse(newTime, formatter);
        }
        return localDateTime;
    }


    @RequestMapping("/reservation")
    public String reservationTimeValidator(@RequestParam(name = "reservationtime", required = false)
                                                       LocalDateTime reservationTime,
                                           @RequestParam("table") Optional<String> table,
                                           Model model) {
        if(table.isPresent()) {
            model.addAttribute("table", table.get());
        }

        if(reservationTime == null) {
            return reservationCurrentTime(model);
        } else {
            return reservationTime(reservationTime, model);
        }
    }

    public String reservationCurrentTime(Model model) {
        return reservationTime(businessTime.getTime(), model);
    }

    public String reservationTime(LocalDateTime localDateTime, Model model) {
        String test = localDateTime.toString();
        model.addAttribute("reservationtime", localDateTime);
        model.addAttribute("reservations", tableToName(getReservatedTablesByTime(localDateTime)));
        return "reservation";
    }

    @RequestMapping("/reservation/add")
    public ModelAndView reservationAdd(@RequestParam("reservationtime") String reservationTime,
                                       @RequestParam("table") String tableName,
                                       @RequestParam("duration") Integer duration,
                                       @RequestParam("name") String name,
                                       ModelAndView mvc) {
        Table table = tables.findByName(tableName);
        LocalDateTime startTime = parseTime(reservationTime);
        LocalDateTime endTime = startTime.plusMinutes(duration);
        TimeInterval timeInterval = new TimeInterval(startTime, endTime);
        Reservation reservation = new Reservation(name, 0, table, timeInterval);
        reservations.save(reservation);

        mvc.setViewName("redirect:/reservation");
        return mvc;
    }



}
