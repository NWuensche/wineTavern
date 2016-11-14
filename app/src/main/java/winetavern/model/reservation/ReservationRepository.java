package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;


/**
 * Created by nwuensche on 05.11.16.
 */

public interface ReservationRepository extends SalespointRepository<Reservation, Long> {
    //public List<Reservation> findByTime(LocalDateTime time);
    public List<Reservation> findByTable(Table table);


    public List<Reservation> findAllByOrderByGuestName();
    public List<Reservation> findAllByOrderByInterval();
    public List<Reservation> findAllByOrderByTable();
    public List<Reservation> findAllByOrderByPersons();
}
