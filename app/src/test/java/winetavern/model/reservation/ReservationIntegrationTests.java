package winetavern.model.reservation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.model.management.TimeInterval;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Niklas Wünsche
 */

@Transactional
public class ReservationIntegrationTests extends AbstractIntegrationTests{

    @Autowired private DeskRepository deskRepository;
    @Autowired private ReservationRepository reservationRepository;

    private Desk desk;
    private Desk desk2;
    private Reservation reservation;
    private TimeInterval interval;

    @Before
    public void before() {
        LocalDateTime start = LocalDateTime.of(2016, 11, 23, 20, 40);
        LocalDateTime end = start.plusHours(3);
        interval = new TimeInterval(start, end);

        desk = new Desk("Tisch 1", 4);
        deskRepository.save(desk);

        reservation = new Reservation("Gast 1", 3, desk, interval);
        reservationRepository.save(reservation);


        desk2 = new Desk("Tisch 2", 6);
        deskRepository.save(desk2);
    }

    @Test
    public void addReservationCorrect() {
        Reservation reservation2 = new Reservation("Peter Mueller", 3, desk2, interval);
        reservationRepository.save(reservation2);
        desk2.addReservation(reservation2);

        List<Reservation> deskList = reservationRepository.findByDesk(desk2);
        assertThat(deskList.contains(reservation2), is(true));
    }

    @Test
    public void oldDeskDeleted() {
        Desk desk2 = new Desk("Tisch 2", 4);
        deskRepository.save(desk2);
        reservation.setDesk(desk2);
        assertThat(reservationRepository.findByDesk(desk2).isEmpty(), is(false));
        assertThat(reservationRepository.findByDesk(desk).isEmpty(), is(true));
    }

}
