package winetavern.controller.sortStrategyReservationController;

import winetavern.model.reservation.Reservation;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Niklas Wünsche
 */

public class NameSortStrategy implements SortStrategy {

    @Override
    public List<Reservation> sort(Iterable<Reservation> allReservations) {
        List<Reservation> sortedReservationsByName = new LinkedList<>();

        allReservations.forEach(sortedReservationsByName::add);

        return sortedReservationsByName;
    }

}
