package winetavern.model.reservation;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import winetavern.model.management.TimeInterval;
import java.time.LocalDateTime;

/**
 * @author Sev, Niklas
 */

public class DeskTests {

    private Desk desk;
    private Reservation reservation;
    private TimeInterval interval;

    @Before
    public void before() {
        LocalDateTime start = LocalDateTime.of(2016, 11, 23, 20, 40);
        LocalDateTime end = start.plusHours(3);
        interval = new TimeInterval(start, end);

        desk = new Desk("Tisch 1", 4);

        reservation = new Reservation("Gast 1", 3, desk, interval);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DeskConstructorNegativeCapacityTest() {
        Desk wrongDesk = new Desk("1", -2);
    }

    @Test
    public void addReservationCorrect() {
        Reservation reservation = mock(Reservation.class);
        desk.addReservation(reservation);

        assertThat(desk.getReservationList().contains(reservation), is(true));
    }

}
