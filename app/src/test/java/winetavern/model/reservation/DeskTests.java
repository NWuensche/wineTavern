package winetavern.model.reservation;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.TimeInterval;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * @author Sev
 */

@Transactional
public class DeskTests extends AbstractIntegrationTests {

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

    @Test(expected = IllegalArgumentException.class)
    public void DeskConstructorNegativeCapacityTest() {
        Desk wrongDesk = new Desk("1", -2);
    }

    @Test
    public void addReservationCorrect() {
        desk2.addReservation(reservation);
        assertThat(reservationRepository.findByDesk(desk2).contains(reservation), is(true));
    }

}
