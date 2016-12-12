package winetavern.controller.handlerReservationController;

import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Niklas Wünsche
 */

public class DontSortStrategy implements SortStrategy {

    @Override
    public List<Reservation> sort(Iterable<Reservation> allReservations) {
        List<Reservation> sortedReservationsByNothing = new LinkedList<>();

        allReservations.forEach(sortedReservationsByNothing::add);

        return sortedReservationsByNothing;
    }

}
