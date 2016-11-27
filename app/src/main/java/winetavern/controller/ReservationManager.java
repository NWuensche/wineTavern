package winetavern.controller;

import org.salespointframework.catalog.Product;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    DeskRepository desks;
    @Autowired ReservationRepository reservations;
    @Autowired BusinessTime businessTime;
    DateTimeFormatter dateTimeFormatter;

    public ReservationManager() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    }

    /**
     * Custom Initbinder makes LocalDateTime working with javascript
     */
    @InitBinder
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor());
    }

    public class LocalDateTimeEditor extends PropertyEditorSupport {

        // Converts a String to a LocalDateTime (when submitting form)
        @Override
        public void setAsText(String text) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);
            setValue(localDateTime);
        }

        // Converts a LocalDateTime to a String (when displaying form)
        // not getting called (fuck spring)
        @Override
        public String getAsText() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String time = ((LocalDateTime)getValue()).format(formatter);
            return time;
        }
    }

    /**
     * Creates a List containing all reserved Tables at the given time. Other desks can be assumed free.
     * ToDo: Currently iterating through nearly all reservations. Make MySQL do the job.
     */
    public List<Desk> getReservatedTablesByTime(LocalDateTime localDateTime) {
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


    @RequestMapping("/service/reservation")
    public ModelAndView reservationTimeValidator(@RequestParam(name = "reservationtime", required = false)
                                                       LocalDateTime reservationTime,
                                           @RequestParam("desk") Optional<String> desk,
                                           ModelAndView modelAndView) {
        if(desk.isPresent()) {
            modelAndView.addObject("desk", desk.get());
            modelAndView.addObject("deskcapacity", desks.findByName(desk.get()).getCapacity());
        }

        if(reservationTime == null) {
            return reservationCurrentTime(modelAndView);
        } else {
            return reservationTime(reservationTime, modelAndView);
        }
    }

    public ModelAndView reservationCurrentTime(ModelAndView modelAndView) {
        return reservationTime(businessTime.getTime(), modelAndView);
    }

    public ModelAndView reservationTime(LocalDateTime localDateTime, ModelAndView modelAndView) {
        modelAndView.addObject("datetimeformatter", dateTimeFormatter);
        modelAndView.addObject("reservationtime", localDateTime);
        modelAndView.addObject("reservations", deskToName(getReservatedTablesByTime(localDateTime)));
        modelAndView.setViewName("reservation");
        return modelAndView;
    }

    @RequestMapping("/service/reservation/add")
    public ModelAndView reservationAdd(@RequestParam("reservationtime") LocalDateTime reservationTime,
                                       @RequestParam("desk") String deskName,
                                       @RequestParam("amount") Integer amount,
                                       @RequestParam("duration") Integer duration,
                                       @RequestParam("name") String name,
                                       ModelAndView mvc) {
        Desk desk = desks.findByName(deskName);
        LocalDateTime endTime = reservationTime.plusMinutes(duration);
        TimeInterval timeInterval = new TimeInterval(reservationTime, endTime);
        Reservation reservation = new Reservation(name, amount, desk, timeInterval);
        reservations.save(reservation);

        mvc.setViewName("redirect:/service/reservation");
        return mvc;
    }

    @RequestMapping("/service/reservation/remove")
    public ModelAndView reservationRemove(@RequestParam("reservation") Long reservationId, ModelAndView mvc) {
        reservations.delete(reservationId);
        mvc.setViewName("redirect:/service/reservation/showall");
        return mvc;
    }


    @RequestMapping(value = "/service/reservation/showall", method = RequestMethod.GET)
    public String showAllReservations(@RequestParam("sort") Optional<String> sort, Model model){
        Iterable<Reservation> reservationIterator;

        List<Reservation> reservationList = new LinkedList<>();
        if(!sort.isPresent()) {
            reservationIterator = reservations.findAll();
            reservationIterator.forEach(reservationList::add);
        } else if(sort.get().equals("date")) {
            reservationIterator = reservations.findAll();
            reservationIterator.forEach(reservationList::add);
            Collections.sort(reservationList, (o1, o2) -> o1.getInterval().getStart().compareTo(o2.getInterval().getStart()));
        } else if(sort.get().equals("name")) {
            reservationIterator = reservations.findAllByOrderByGuestName();
            reservationIterator.forEach(reservationList::add);
        } else if(sort.get().equals("persons")) {
            reservationIterator = reservations.findAllByOrderByPersons();
            reservationIterator.forEach(reservationList::add);
        } else {
            reservationIterator = reservations.findAll();
            reservationIterator.forEach(reservationList::add);
        }


        reservationList = selectGreaterThanNow(reservationList);
        model.addAttribute("reservationAmount", reservationList.size());
        model.addAttribute("reservationList", reservationList);
        return "allreservations";
    }

    private List<Reservation> selectGreaterThanNow(List<Reservation> list) {
        List<Reservation> res = new LinkedList<>();
        for (Reservation reservation: list)
            if (reservation.getInterval().getEnd().isAfter(businessTime.getTime()))
                res.add(reservation);

        return res;
    }

    private Map<LocalDateTime, Desk> getFreeDesks(LocalDateTime time, int capacity) {
        Map<LocalDateTime, Desk> res = new TreeMap<>();


        Iterable<Reservation> allReservations = reservations.findAll();
        List<LocalDateTime> offset = new ArrayList<>();
        offset.add(time);
        for(int i = 1; i < 6; i++) {
            offset.add(time.plusMinutes(i * 30));
            offset.add(time.minusMinutes(i * 30));
        }
        for(LocalDateTime i : offset){
            Iterable<Desk> allTables = desks.findByCapacityGreaterThanEqualOrderByCapacity(capacity);
            List<Desk> tableList = new ArrayList<>();
            allTables.forEach(tableList::add);
            for (Reservation reservation : allReservations) {
                TimeInterval interval = new TimeInterval(i, i.plusMinutes(150));
                if(interval.intersects(reservation.getInterval())){
                    tableList.remove(reservation.getDesk());
                }
            }
            if(i.equals(time) && !tableList.isEmpty()){
                res.put(time, tableList.get(0));
                return res;
            }
            if(!tableList.isEmpty()) res.put(i,tableList.get(0));
        }

        return res;
    }



}
