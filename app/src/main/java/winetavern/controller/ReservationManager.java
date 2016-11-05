package winetavern.controller;

import com.sun.javaws.exceptions.InvalidArgumentException;
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

    public void addReservation(Reservation reservation) throws NullPointerException{
        if(reservation == null){
            System.out.println("ERROR");
            throw new NullPointerException("ReservationManager can't add null to reservation set.");
        }

        reservations.add(reservation);
    }

    public void changeReservation(int id, String guestName, Table table, LocalDateTime time, Duration duration)
            throws InvalidArgumentException {
        Reservation reservation = getReservationById(id);

        if(reservation==null) {
            throw new InvalidArgumentException(new String[]{"Unknown Reservation ID in reservation.ReservationManager.changeReservation()"});
        }

        reservation.setGuestName(guestName);
        reservation.setTable(table);
        reservation.setTime(time);
        reservation.setDuration(duration);
    }

    public void removeReservation(Reservation reservation) throws NullPointerException{
        if(reservation == null){
            throw new NullPointerException("ReservationManager can't remove null from reservation set.");
        }

        reservations.remove(reservation);
    }

    public Set<Table> getFreeTablesByTime(LocalDateTime time){
        Set<Table> freeTables = new HashSet<Table>();
        for(Reservation reservation:reservations) {
            if ((reservation.getTime().compareTo(time) < 0) && (reservation.getTime().plus(reservation.getDuration()).compareTo(time) < 0)) {
                freeTables.add(reservation.getTable());
            }
        }
        return freeTables;
    }

    public Reservation getReservationById(long id){
        for(Reservation reservation:reservations){
            if(reservation.getId()==id){
                return reservation;
            }
        }
        System.out.println("Reservation Manager: nothing found for id" + id);
        return null;
    }
}
