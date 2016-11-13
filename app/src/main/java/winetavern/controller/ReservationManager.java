package winetavern.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Table;
import winetavern.model.reservation.TableRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sev
 */
@Controller
public class ReservationManager {

    @Autowired
    TableRepository tables;
    @Autowired
    ReservationRepository reservations;

    @RequestMapping(value="/reservation",method = RequestMethod.POST)
    public String newReservation(@RequestParam("datetime") Optional<String> datetime, @RequestParam("persons")
            Optional<Integer> persons, @RequestParam("tableid") Optional<Long> table, @RequestParam("name")
                                 Optional<String> name, @RequestParam("check") Optional<String> check, @RequestParam
            ("submitdata") Optional<String> submit, @RequestParam("option") Optional<String> option, ModelMap model){

        if(check.isPresent() && datetime.isPresent() && persons.isPresent()){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(datetime.get(), formatter);

            Map<Integer,Table> freeTables = getFreeTables(start,persons.get().intValue());

            model.addAttribute("datetime",datetime.get());
            model.addAttribute("persons",persons.get().intValue());
            model.put("tableMap",freeTables);
            freeTables.isEmpty();

        } else if(submit.isPresent() && datetime.isPresent() && persons.isPresent() && name
                .isPresent() && option.isPresent()){

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(datetime.get(), formatter);
            LocalDateTime end = start.plusHours(2).plusMinutes(30);

            String[] args = option.get().split(":"); //tableNumber:offset
            System.out.println("T:O: " + Integer.parseInt(args[0]) + ':' + Integer.parseInt(args[1]));
            TimeInterval interval = new TimeInterval(start,end);
            interval.moveIntervalByMinutes(Integer.parseInt(args[1]));
            Reservation reservation = new Reservation(name.get(),
                    tables.findByNumber(Integer.parseInt(args[0])).get(0), interval);
            reservations.save(reservation);

            model.addAttribute("success", "Reservierung wurde gespeichert");
            model.addAttribute("userdata", persons.get() + " Personen an Tisch " + args[0] + " " + "auf " +
                    "den Namen " + name.get() + " für " + interval.getStart());
            return "reservation";
        }

        return "reservation";
    }

    @RequestMapping("/reservation")
    public String showReservationTemplate(){return "reservation";}

    private Map<Integer, Table> getFreeTables(LocalDateTime time,int capacity) {
        Map<Integer, Table> res = new HashMap<>();

        TimeInterval interval = new TimeInterval(time, time.plusHours(2).plusMinutes(30));
        Iterable<Reservation> allReservations = reservations.findAll();
        List<Integer> offset = new LinkedList<>();
        offset.add(0);
        for(int i = 1; i < 6; i++) {
            offset.add(i * 30);
            offset.add(i * -30);
        }
        for(Integer i:offset){
            Iterable<Table> allTables = tables.findByCapacityGreaterThanEqualOrderByCapacity(capacity);
            List<Table> tableList = new ArrayList();
            allTables.forEach(tableList::add);
            for (Reservation reservation : allReservations) {
                TimeInterval tempInterval = new TimeInterval(interval.getStart(), interval.getEnd());
                tempInterval.moveIntervalByMinutes(i);
                if(TimeInterval.intersects(reservation.getInterval(),tempInterval)){
                    tableList.remove(reservation.getTable());
                }
            }
            if(i == 0 && !tableList.isEmpty()){
                res.put(0,tableList.get(0));
                return res;
            }
            if(!tableList.isEmpty()) res.put(i,tableList.get(0));
        }

        return res;
    }
}
