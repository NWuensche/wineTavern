package winetavern.controller.handlerReservationController;

import org.springframework.beans.factory.annotation.Autowired;
import winetavern.controller.ReservationManager;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Niklas Wünsche
 */

public class DateHandler implements Handler {

    @Autowired private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> handle() {
        Iterable<Reservation> allReservations;
        List<Reservation> sortedReservationsByDate = new LinkedList<>();

        allReservations = reservationRepository.findAll();
        allReservations.forEach(sortedReservationsByDate::add);
        Collections.sort(sortedReservationsByDate, (o1, o2) -> o1.getInterval().getStart().compareTo(o2.getInterval().getStart()));

        return sortedReservationsByDate;
    }

}
