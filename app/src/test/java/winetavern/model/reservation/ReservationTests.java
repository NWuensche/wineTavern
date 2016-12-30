package winetavern.model.reservation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import com.mysql.cj.core.exceptions.NumberOutOfRange;
import org.junit.Before;
import org.junit.Test;
import winetavern.model.management.TimeInterval;
import java.time.LocalDateTime;

/**
 * @author Niklas Wünsche
 */
public class ReservationTests {

    private Reservation reservation;
    private Desk desk;
    private TimeInterval mockedTimeInterval;

    @Before
    public void before() {
        mockedTimeInterval = mock(TimeInterval.class);
        desk = new Desk("Tisch 1", 3);

        reservation = new Reservation("Gast 1", 3, desk, mockedTimeInterval);
    }

    @Test
    public void setNewDesk() {
        Given:
        assertThat(desk.getReservationList().contains(reservation), is(true));
        Desk newDesk = new Desk("Tisch New", 3);

        When:
        reservation.setDesk(newDesk);

        Then:
        assertThat(desk.getReservationList().contains(reservation), is(false));
        assertThat(reservation.getDesk(), is(newDesk));
        assertThat(newDesk.getReservationList().contains(reservation), is(true));
    }

    @Test(expected = NumberOutOfRange.class)
    public void throwWhenNegativePersons() {
        new Reservation("Gast", -1, desk, mockedTimeInterval);
    }

    @Test(expected = NumberOutOfRange.class)
    public void throwWhenSetNegativePersons() {
        reservation.setPersons(3);

        reservation.setPersons(-1);
    }

    @Test
    public void getStartRight() {
        LocalDateTime start = LocalDateTime.now();
        when(mockedTimeInterval.getStart()).thenReturn(start);

        assertThat(reservation.getReservationStart(), is(start));
    }

}
