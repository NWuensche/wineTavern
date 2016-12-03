package winetavern.model.reservation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import winetavern.model.management.TimeInterval;
import java.time.LocalDateTime;

/**
 * @author Niklas Wünsche
 */

public class ReservationTests {

    private Reservation reservation;
    private Desk mockedDesk;
    private TimeInterval mockedTimeInterval;

    @Before
    public void before() {
        mockedTimeInterval = mock(TimeInterval.class);
        mockedDesk = mock(Desk.class);

        reservation = new Reservation("Gast 1", 3, mockedDesk, mockedTimeInterval);
    }

    @Test
    public void setNewDesk() {
        Desk newDesk = mock(Desk.class);

        reservation.setDesk(newDesk);

        assertThat(reservation.getDesk(), is(newDesk));
    }

    @Test
    public void getStartRight() {
        LocalDateTime start = LocalDateTime.now();
        when(mockedTimeInterval.getStart()).thenReturn(start);

        assertThat(reservation.getReservationStart(), is(start));
    }

}
