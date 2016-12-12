package winetavern.controller.sortStrategyReservationController;

import winetavern.model.reservation.Reservation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Niklas Wünsche
 */

public class DateSortStrategy implements SortStrategy {

    @Override
    public List<Reservation> sort(Iterable<Reservation> allReservations) {
        List<Reservation> sortedReservationsByDate = new LinkedList<>();

        allReservations.forEach(sortedReservationsByDate::add);
        Collections.sort(sortedReservationsByDate, (res1, res2) -> res1.getInterval().getStart().compareTo(res2.getInterval().getStart()));

        return sortedReservationsByDate;
    }

}
