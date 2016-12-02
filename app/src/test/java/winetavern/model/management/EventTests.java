package winetavern.model.management;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.lessThan;

import org.javamoney.moneta.Money;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link Event}
 * @author Louis
 */

public class EventTests {
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(3);
    private Money money = Money.of(7, EURO);
    private String description = "description";

    @Test(expected = NullPointerException.class)
    public void throwWhenNullInterval() {
        new Event("Event", money, null, description);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullDescription() {
        new Event("Event", money, new TimeInterval(start, end), null);
    }

    @Test(expected = IllegalStateException.class)
    public void throwWhenEmptyDescription() {
        new Event("Event", money,  new TimeInterval(start, end), "");
    }

    // TODO Mock TimeInterval
    @Test
    public void compareRight() {
        LocalDateTime laterStart = start.plusHours(3);
        LocalDateTime laterEnd = laterStart.plusHours(3);

        Event event = new Event("Event", money, new TimeInterval(start, end), description);
        Event laterEvent = new Event("Event", money, new TimeInterval(laterStart, laterEnd), description);

        assertThat(event.compareTo(laterEvent), is(lessThan(0)));
    }
}
