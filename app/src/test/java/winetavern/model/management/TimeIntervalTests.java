package winetavern.model.management;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

import java.time.LocalDateTime;

/**
 * Test class for {@link TimeInterval}
 * @author Louis
 */

public class TimeIntervalTests extends AbstractWebIntegrationTests {
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(3);

    @Test(expected = NullPointerException.class)
    public void throwWhenNullTime() {
        new TimeInterval(start, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNoDuration() {
        new TimeInterval(start, start);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeDuration() {
        new TimeInterval(end, start);
    }
}
