package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by nwuensche on 05.11.16.
 */

public interface ReservationRepository extends SalespointRepository<Reservation, Long> {
    public List<Reservation> findByTime(LocalDateTime time);
}
