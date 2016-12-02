package winetavern.model.management;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
/**
 * Test class for {@link TimeInterval}
 * @author Louis
 */
public class TimeIntervalTests {
    private LocalDateTime start;
    private LocalDateTime end;

    @Before
    public void before() {
        start = LocalDateTime.now();
        end = start.plusHours(3);
    }


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

        assertThat(first.intersects(second), is(true));
        assertThat(second.intersects(first), is(true));
        assertThat(first.intersects(third), is(false));
        assertThat(third.intersects(first), is(false));
    }

}
