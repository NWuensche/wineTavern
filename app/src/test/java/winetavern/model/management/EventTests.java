package winetavern.model.management;

import static org.mockito.Mockito.*;
import static org.salespointframework.core.Currencies.EURO;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import winetavern.model.user.External;

/**
 * Test class for {@link Event}
 * @author Louis, Niklas
 */
public class EventTests {

    private TimeInterval mockedTimeInterval;
    private Money money;
    private String description;
    private External external;

    @Before
    public void before() {
        mockedTimeInterval = mock(TimeInterval.class);

        money = Money.of(7, EURO);
        description = "description";
        external = new External("der Neue", Money.of(180, EURO));
    }

    @Test
    public void createEvent() {
        new Event("Event", money, mockedTimeInterval, description, external);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullInterval() {
        new Event("Event", money, null, description, external);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNullDescription() {
        new Event("Event", money, mockedTimeInterval, null, external);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, "", external);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenEmptyExternal() {
        new Event("Event", money, mockedTimeInterval, "Event", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenSetEmptyDescription() {
        new Event("Event", money, mockedTimeInterval, description, external).setDescription("");
    }

}
