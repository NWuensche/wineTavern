package winetavern.model.management;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;

import java.sql.Time;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link TimeInterval}
 * @author Louis
 */

public class TimeIntervalTests {
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(3);

    @Test(expected = NullPointerException.class)
    public void throwWhenNullTime() {
        new TimeInterval(start, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeDuration() {
        new TimeInterval(end, start);
    }

    @Test
    public void intersectionTest(){
        TimeInterval first = new TimeInterval(start,end);
        TimeInterval second = new TimeInterval(start.plusHours(1),start.plusHours(4));
        TimeInterval third = new TimeInterval(end.plusHours(2),end.plusHours(5));

        assertTrue(first.intersects(second));
        assertTrue(second.intersects(first));
        assertFalse(first.intersects(third));
        assertFalse(third.intersects(first));
    }
}
