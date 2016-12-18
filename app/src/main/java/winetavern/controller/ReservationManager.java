package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import winetavern.controller.sortStrategyReservationController.*;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.Desk;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sev, Michel Kunkler
 */
@Controller
public class ReservationManager {

    @Autowired DeskRepository desks;
    @Autowired ReservationRepository reservations;
    @Autowired BusinessTime businessTime;
    DateTimeFormatter dateTimeFormatter;

    public ReservationManager() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    }


    /**
     * Creates a List containing all reserved Tables at the given time. Other desks can be assumed free.
     * ToDo: Currently iterating through nearly all reservations. Make MySQL do the job.
     */
    public List<Desk> getReservatedTablesByTime(LocalDateTime localDateTime) {
        List<Desk> reservedDesks = new ArrayList<Desk>();

        for(Desk desk : desks.findAll()) {
            for(Reservation reservation : desk.getReservationList()) {
                if(isActive(reservation, localDateTime)) {
                    reservedDesks.add(desk);
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

        deskList.forEach(desk -> nameList.add(desk.getName()));

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
    public ModelAndView reservationTimeValidator(@RequestParam("form") Optional<String> formType,
                                                 @RequestParam("reservationtime") Optional<String> reservationTimeString,
                                                 @RequestParam("sort") Optional<String> sort,
                                                 @RequestParam("desk") Optional<String> deskName,
                                           ModelAndView modelAndView) {
        LocalDateTime reservationTime = reservationTimeString.isPresent() ?
                LocalDateTime.parse(reservationTimeString.get(), dateTimeFormatter) :
                businessTime.getTime();

        if(deskName.isPresent()) {
            modelAndView.addObject("desk", deskName.get());
            Desk desk = desks.findByName(deskName.get());
            //show only future reservations
            List<Reservation> reservations = desk.getReservationList();
            List<Reservation> finalReservations = new ArrayList<>();
            reservations.forEach((reservation) -> {
                if(!reservation.getReservationEnd().isBefore(reservationTime))
                    finalReservations.add(reservation);
            });
            modelAndView.addObject("deskReservations", finalReservations);
            modelAndView.addObject("deskcapacity", desk.getCapacity());
        }

        reservationTableData(sort, reservationTime, modelAndView);
        return reservationTime(reservationTime, modelAndView);
    }

    public ModelAndView reservationTime(LocalDateTime localDateTime, ModelAndView modelAndView) {
        modelAndView.addObject("datetimeformatter", dateTimeFormatter);
        modelAndView.addObject("reservationtime", localDateTime);
        modelAndView.addObject("reservations", deskToName(getReservatedTablesByTime(localDateTime)));
        modelAndView.setViewName("reservation");
        return modelAndView;
    }

    @RequestMapping("/service/reservation/add")
    public ModelAndView reservationAdd(@RequestParam("reservationtime") String reservationTimeString,
                                       @RequestParam("desk") String deskName,
                                       @RequestParam("amount") Integer amount,
                                       @RequestParam("duration") Integer duration, // In Minutes
                                       @RequestParam("name") String name,
                                       ModelAndView modelAndView) {
        LocalDateTime reservationTime = LocalDateTime.parse(reservationTimeString, dateTimeFormatter);
        Desk desk = desks.findByName(deskName);
        LocalDateTime endTime = reservationTime.plusMinutes(duration);
        TimeInterval timeInterval = new TimeInterval(reservationTime, endTime);
        Reservation reservation = new Reservation(name, amount, desk, timeInterval);
        reservations.save(reservation);

        modelAndView.setViewName("redirect:/service/reservation");
        return modelAndView;
    }

    @RequestMapping("/service/reservation/remove")
    public ModelAndView reservationRemove(@RequestParam("reservation") Long reservationId,
                                          ModelAndView modelAndView) {
        reservations.delete(reservationId);
        modelAndView.setViewName("redirect:/service/reservation");
        return modelAndView;
    }

    /**
     * returns data for the table view.
     */
    public ModelAndView reservationTableData(Optional<String> sort,
                                             LocalDateTime time,
                                             ModelAndView modelAndView) {
        Map<String, SortStrategy> sortStrategyMap = createSortMap();
        String sortBy = sort.orElse("nothing");
        List<Reservation> reservationList = sortStrategyMap.get(sortBy).sort(reservations.findAll());

        reservationList = pickLater(time, reservationList);

        modelAndView.addObject("reservationTableList", reservationList);
        return modelAndView;
    }

    private Map<String, SortStrategy> createSortMap() {
        Map<String, SortStrategy> strategyMap = new HashMap<>();
        strategyMap.put("nothing", new DontSortStrategy());
        strategyMap.put("date", new DateSortStrategy());
        strategyMap.put("name", new NameSortStrategy());
        strategyMap.put("persons", new PersonSortStrategy());

        return  strategyMap;
    }

    private List<Reservation> pickLater(LocalDateTime time, List<Reservation> list) {
        List<Reservation> res = new LinkedList<>();
        for (Reservation reservation: list)
            if (reservation.getInterval().getEnd().isAfter(time))
                res.add(reservation);

        return res;
    }

}
