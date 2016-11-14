package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sev
 */
@Controller
public class ReservationManager {

    @Autowired TableRepository tables;
    @Autowired ReservationRepository reservations;
    @Autowired BusinessTime businessTime;

    @RequestMapping(value="/reservation",method = RequestMethod.POST)
    public String newReservation(@RequestParam("datetime") Optional<String> datetime, @RequestParam("persons")
            Optional<Integer> persons, @RequestParam("tableid") Optional<Long> table, @RequestParam("name")
            Optional<String> name, @RequestParam("check") Optional<String> check, @RequestParam("submitdata")
            Optional<String> submit, @RequestParam("option") Optional<String> option, ModelMap model) {

        if(check.isPresent() && datetime.isPresent() && persons.isPresent()){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(datetime.get(), formatter);

            Map<LocalDateTime,Table> freeTables = getFreeTables(start,persons.get().intValue());

            model.addAttribute("datetime",datetime.get());
            model.addAttribute("persons",persons.get().intValue());
            model.put("tableMap",freeTables);

        } else if(submit.isPresent() && datetime.isPresent() && persons.isPresent() && name
                .isPresent() && option.isPresent()){

            String[] args = option.get().split(","); //tableId,localDateTime
            LocalDateTime start = LocalDateTime.parse(args[1].replace("T", " "),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = start.plusHours(2).plusMinutes(30);



            TimeInterval interval = new TimeInterval(start,end);
            Reservation reservation = new Reservation(name.get(),persons.get(),
                    tables.findOne(Long.parseLong(args[0])).get(), interval);

            reservations.save(reservation);

            model.addAttribute("success", "Reservierung wurde gespeichert");
            model.addAttribute("userdata", persons.get() + " Personen an Tisch " +
                    tables.findOne(Long.parseLong(args[0])).get().getNumber() + " " + "auf " +
                    "den Namen " + name.get() + " für " + start.toLocalTime());
            return "reservation";
        }

        return "reservation";
    }


    @RequestMapping("/reservation")
    public String showReservationTemplate(){return "reservation";}


    @RequestMapping(value = "/allReservations", method = RequestMethod.GET)
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
        } else if(sort.get().equals("table")) {
            reservationIterator = reservations.findAll();
            reservationIterator.forEach(reservationList::add);
            Collections.sort(reservationList, (o1, o2) -> Integer.compare(o1.getTable().getNumber(), o2.getTable().getNumber()));
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

    private Map<LocalDateTime, Table> getFreeTables(LocalDateTime time, int capacity) {
        Map<LocalDateTime, Table> res = new TreeMap<>();


        Iterable<Reservation> allReservations = reservations.findAll();
        List<LocalDateTime> offset = new ArrayList<>();
        offset.add(time);
        for(int i = 1; i < 6; i++) {
            offset.add(time.plusMinutes(i * 30));
            offset.add(time.minusMinutes(i * 30));
        }
        for(LocalDateTime i : offset){
            Iterable<Table> allTables = tables.findByCapacityGreaterThanEqualOrderByCapacity(capacity);
            List<Table> tableList = new ArrayList<>();
            allTables.forEach(tableList::add);
            for (Reservation reservation : allReservations) {
                TimeInterval interval = new TimeInterval(i, i.plusMinutes(150));
                if(TimeInterval.intersects(reservation.getInterval(), interval)){
                    tableList.remove(reservation.getTable());
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
