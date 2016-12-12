package winetavern.controller.sortStrategyReservationController;

import winetavern.model.reservation.Reservation;

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
