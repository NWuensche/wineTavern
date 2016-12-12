package winetavern.controller.handlerReservationController;

import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Niklas Wünsche
 */

public class PersonSortStrategy implements SortStrategy {


    @Override
    public List<Reservation> sort(Iterable<Reservation> allReservations) {
        List<Reservation> sortedReservationsByPerson = new LinkedList<>();

        allReservations.forEach(sortedReservationsByPerson::add);
        Collections.sort(sortedReservationsByPerson, (res1, res2) -> res1.getPersons() - res2.getPersons());

        return sortedReservationsByPerson;
    }

}
