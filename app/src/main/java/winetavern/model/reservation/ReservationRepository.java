package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;
import winetavern.ExtraRepository;

import java.util.List;


/**
 * @author Sev
 */

public interface ReservationRepository extends ExtraRepository<Reservation, Long> {
    public List<Reservation> findByDesk(Desk desk);
    public List<Reservation> findAllByOrderByGuestName();
    public List<Reservation> findAllByOrderByDesk();
    public List<Reservation> findAllByOrderByPersons();
}
