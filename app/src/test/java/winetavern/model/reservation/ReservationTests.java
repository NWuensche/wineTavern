package winetavern.model.reservation;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import com.mysql.cj.core.exceptions.NumberOutOfRange;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.model.management.TimeInterval;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * Created by nwuensche on 29.11.16.
 */

@Transactional
public class ReservationTests extends AbstractIntegrationTests {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private DeskRepository deskRepository;

    private Reservation reservation;
    private Desk desk;
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
