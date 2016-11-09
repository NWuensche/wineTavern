package winetavern.model.management;

import org.javamoney.moneta.Money;
import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

import java.time.LocalDateTime;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Test class for {@link Event}
 * @author Louis
 */

public class EventTests extends AbstractWebIntegrationTests {
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(3);
    private Money money = Money.of(7, EURO);

    @Test(expected = NullPointerException.class)
    public void throwWhenNullInterval() {
        new Event("Event", money, null, "");
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullDescription() {
        new Event("Event", money, new TimeInterval(start, end), null);
    }
}
