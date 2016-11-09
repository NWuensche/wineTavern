package winetavern.controller;

import org.springframework.stereotype.Controller;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sev
 */

@Controller
public class ReservationManager {

    private Set<Reservation> reservations;

    public ReservationManager() {
        reservations = new HashSet<Reservation>();
    }

    public Set<Table> getFreeTablesByTime(LocalDateTime time) {
        Set<Table> freeTables = new HashSet<Table>();
        for(Reservation reservation : reservations) {
            if ((reservation.getTime().compareTo(time) < 0) && (reservation.getTime().plus(reservation.getDuration()).compareTo(time) < 0)) {
                freeTables.add(reservation.getTable());
            }
        }
        return freeTables;
    }

}
