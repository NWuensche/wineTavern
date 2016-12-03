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

    private TimeInterval mockedTimeInterval;
    private Money money;
    private String description;

    @Before
    public void before() {
        mockedTimeInterval = mock(TimeInterval.class);

        money = Money.of(7, EURO);
        description = "description";
    }

    @Test
    public void createEvent() {
        new Event("Event", money, mockedTimeInterval, description);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullInterval() {
        new Event("Event", money, null, description);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullDescription() {
        new Event("Event", money, mockedTimeInterval, null);
    }

    @Test(expected = IllegalStateException.class)
    public void throwWhenEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenSetEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, description).setDescription("");
    }

    @Test
    public void compareRight() {
        TimeInterval mockedEarly = mock(TimeInterval.class);
        TimeInterval mockedLater = mock(TimeInterval.class);

        when(mockedEarly.getStart()).thenReturn(LocalDateTime.now());
        when(mockedLater.getStart()).thenReturn(LocalDateTime.now().plusHours(3));

        Event event = new Event("Event", money, mockedEarly, description);
        Event laterEvent = new Event("Event", money, mockedLater, description);

        assertThat(event.compareTo(laterEvent), is(lessThan(0)));
    }

}
