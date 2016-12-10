package winetavern.model.management;

import static org.mockito.Mockito.*;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;

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

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, "");
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenSetEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, description).setDescription("");
    }

}
