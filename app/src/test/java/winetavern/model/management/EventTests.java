package winetavern.model.management;

import static org.mockito.Mockito.*;
import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.lessThan;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link Event}
 * @author Louis, Niklas
 */

public class EventTests {

    private TimeInterval timeInterval;
    private Money money;
    private String description;

    @Before
    public void before() {
        timeInterval = mock(TimeInterval.class);

        money = Money.of(7, EURO);
        description = "description";
    }

    @Test
    public void createEvent() {
        new Event("Event", money, timeInterval, description);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullInterval() {
        new Event("Event", money, null, description);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullDescription() {
        new Event("Event", money, timeInterval, null);
    }

    @Test(expected = IllegalStateException.class)
    public void throwWhenEmptyDescription() {
        new Event("Event", money, timeInterval, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenSetEmptyDescription() {
        new Event("Event", money, timeInterval, description).setDescription("");
    }

    @Test
    public void compareRight() {
        TimeInterval early = mock(TimeInterval.class);
        TimeInterval later = mock(TimeInterval.class);

        when(early.getStart()).thenReturn(LocalDateTime.now());
        when(later.getStart()).thenReturn(LocalDateTime.now().plusHours(3));

        Event event = new Event("Event", money, early, description);
        Event laterEvent = new Event("Event", money, later, description);

        assertThat(event.compareTo(laterEvent), is(lessThan(0)));
    }

}
