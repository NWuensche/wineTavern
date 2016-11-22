package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sev, Michel Kunkler
 */
@Controller
public class ReservationManager {

    @Autowired
    DeskRepository desks;
    @Autowired ReservationRepository reservations;
    @Autowired BusinessTime businessTime;


    /**
     * Creates a List containing all reserved Tables at the given time. Other desks can be assumed free.
     * ToDo: Currently iterating through nearly all reservations. Make MySQL do the job.
     */
    public List<Desk> getReservatedTablesByTime(LocalDateTime localDateTime) {
        //localDateTime.format(DateTimeFormatter.ISO_DATE)
        Iterable<Desk> allDesks = desks.findAll();
        List<Desk> reservedDesks = new ArrayList<Desk>();
        for(Iterator<Desk> deskIterator = allDesks.iterator(); deskIterator.hasNext(); ) {
            Desk currentDesk = deskIterator.next();
            List<Reservation> reservationsOnCurrentDesk = currentDesk.getReservationList();


            for( Iterator<Reservation> reservationsIterator = reservationsOnCurrentDesk.iterator();
                    reservationsIterator.hasNext(); ) {
                Reservation currentReservation = reservationsIterator.next();
                if( isActive(currentReservation, localDateTime) ) {
                    reservedDesks.add(currentDesk);
                    break;
                }
            }
        }
        return reservedDesks;
    }

    /**
     * makes thymeleaf working with the desk object by removing everything but the name :)
     */
    public List<String> deskToName(List<Desk> deskList) {
        List<String> nameList = new ArrayList<String>();
        for(Iterator<Desk> deskIterator = deskList.iterator(); deskIterator.hasNext(); ) {
            Desk currentDesk = deskIterator.next();
            nameList.add(currentDesk.getNumber());
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
    public String reservationTimeValidator(@RequestParam("reservationtime") Optional<String> reservationTime,
                                           @RequestParam("desk") Optional<String> desk,
                                           Model model) {
        if(desk.isPresent()) {
            model.addAttribute("desk", desk.get());
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
        model.addAttribute("reservations", deskToName(getReservatedTablesByTime(localDateTime)));
        return "reservation";
    }

    @RequestMapping("/reservation/add")
    public ModelAndView reservationAdd(@RequestParam("reservationtime") String reservationTime,
                                       @RequestParam("desk") String deskName,
                                       @RequestParam("duration") Integer duration,
                                       @RequestParam("name") String name,
                                       ModelAndView mvc) {
        Desk desk = desks.findByName(deskName);
        LocalDateTime startTime = parseTime(reservationTime);
        LocalDateTime endTime = startTime.plusMinutes(duration);
        TimeInterval timeInterval = new TimeInterval(startTime, endTime);
        Reservation reservation = new Reservation(name, 0, desk, timeInterval);
        reservations.save(reservation);

        mvc.setViewName("redirect:/reservation");
        return mvc;
    }



}
