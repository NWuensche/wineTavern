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

    @Before
    public void before() {
        desk = new Desk("Tisch 1", 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DeskConstructorNegativeCapacityTest() {
        new Desk("1", -2);
    }

    @Test
    public void addReservationCorrect() {
        Reservation reservation = mock(Reservation.class);
        desk.addReservation(reservation);

        assertThat(desk.getReservationList().contains(reservation), is(true));
    }

}
