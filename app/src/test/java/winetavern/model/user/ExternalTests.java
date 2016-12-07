package winetavern.model.user;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import winetavern.model.management.Event;
import winetavern.model.management.TimeInterval;

import javax.money.MonetaryAmount;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * @author Niklas Wünsche
 */

@Transactional
public class ExternalTests {

    private Event event;
    private External external;
    private TimeInterval timeInterval;
    private MonetaryAmount wage;

    @Before
    public void before() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(3);
        timeInterval = new TimeInterval(start, end);

        wage = Money.of(300, EURO);
        external = new External("name", wage);
        event = new Event("testEvent", Money.of(10, EURO), timeInterval, "new testEvent", external);

    }

    @Test
    public void standardFunctionsRight() {
        assertThat(external.getName(), is("name"));
        assertThat(external.getWage(), is(wage));
        assertThat(external.toString(), is("name"));
    }

}
