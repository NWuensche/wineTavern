package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.*;

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
     * Creates a List containing all reserved Tables at the givven time. Other tables can be assumed free.
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

    public boolean isActive(Reservation reservation, LocalDateTime localDateTime) {
        return reservation.getInterval().timeInInterval(localDateTime);
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
    public String reservationTimeValidator(@RequestParam("reservationtime") Optional<String> reservationTime,
                                           @RequestParam("table") Optional<String> table,
                                           Model model) {
        if(table.isPresent()) {
            model.addAttribute("table", table.get());
        }

        if(reservationTime.isPresent() == false || reservationTime.get() == "") {
            return reservationCurrentTime(model);
        } else {
            LocalDateTime localDateTime = parseTime(reservationTime.get());
            return reservationTime(localDateTime, model);
        }
    }

    public String reservationCurrentTime(Model model) {
        return reservationTime(businessTime.getTime(), model);
    }

    public String reservationTime(LocalDateTime localDateTime, Model model) {
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
