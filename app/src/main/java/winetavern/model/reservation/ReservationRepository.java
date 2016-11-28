package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;


/**
 * @author Sev
 */

public interface ReservationRepository extends SalespointRepository<Reservation, Long> {
    public List<Reservation> findByDesk(Desk desk);
    public List<Reservation> findAllByOrderByGuestName();
    public List<Reservation> findAllByOrderByPersons();
}
