package winetavern.controller;

import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sev
 */
public class ReservationManager {
    private Set<Reservation> reservations;

    public ReservationManager() {
        reservations = new HashSet<Reservation>();
    }

    public void addReservation(Reservation reservation){
        reservations.add(reservation);
    }

    public void changeReservation(){

    }

    public void removeReservation(Reservation reservation){
        reservations.remove(reservation);
    }

    public Set<Table> getFreeTablesByTime(LocalDateTime time){
        Set<Table> freeTables = new HashSet<Table>();
        for(Reservation reservation:reservations){
            if((reservation.getDate().compareTo(time)<0) && (reservation.getDate().plus(reservation.getDuration())
                    .compareTo(time)<0)){
                freeTables.add(reservation.getTable());
            }
        }

        return freeTables;
    }
}
