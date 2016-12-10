package winetavern.model.management;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.time.Interval;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Test class for {@link TimeInterval}
 * @author Louis
 */
public class TimeIntervalTests {

    private TimeInterval timeInterval;
    private LocalDateTime start;
    private LocalDateTime end;

    @Before
    public void before() {
        start = LocalDateTime.now();
        end = start.plusHours(3);

        timeInterval = new TimeInterval(start, end);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenStartIsNull() {
        new TimeInterval(start, null);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenEndIsNull() {
        new TimeInterval(start, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeDuration() {
        new TimeInterval(end, start);
    }

    @Test
    public void rightDuration() {
        assertThat(timeInterval.getDuration(), is(Duration.ofHours(3)));
    }

    @Test
    public void createRightInterval() {
        assertThat(timeInterval.toInterval(), is(Interval.from(start).to(end)));
    }

    @Test
    public void moveRight() {
        timeInterval.moveIntervalByMinutes(10);
        assertThat(timeInterval.toInterval(), is(Interval.from(start.plusMinutes(10)).to(end.plusMinutes(10))));

        timeInterval.moveIntervalByMinutes(-20);
        assertThat(timeInterval.toInterval(), is(Interval.from(start.plusMinutes(-10)).to(end.plusMinutes(-10))));
    }

    @Test
    public void intersectsRight() {
        TimeInterval startsBeforeEndsBefore = new TimeInterval(start.minusHours(5), end.minusHours(5));
        assertThat(timeInterval.intersects(startsBeforeEndsBefore), is(false));

        TimeInterval startsBeforeEndsOnStart = new TimeInterval(start.minusHours(5), start);
        assertThat(timeInterval.intersects(startsBeforeEndsOnStart), is(false));

        TimeInterval startsBeforeEndsIn = new TimeInterval(start.minusHours(5), end.minusHours(1));
        assertThat(timeInterval.intersects(startsBeforeEndsIn), is(true));

        TimeInterval startsBeforeEndsOnEnd = new TimeInterval(start.minusHours(5), end);
        assertThat(timeInterval.intersects(startsBeforeEndsOnEnd), is(true));

        TimeInterval startsBeforeEndsAfter = new TimeInterval(start.minusHours(5), end.plusHours(3));
        assertThat(timeInterval.intersects(startsBeforeEndsAfter), is(true));

        TimeInterval startsOnStartEndsIn = new TimeInterval(start, end.minusHours(1));
        assertThat(timeInterval.intersects(startsOnStartEndsIn), is(true));

        TimeInterval startsOnStartEndsOnEnd = timeInterval;
        assertThat(timeInterval.intersects(startsOnStartEndsOnEnd), is(true));

        TimeInterval startsOnStartEndsAfter = new TimeInterval(start, end.plusHours(3));;
        assertThat(timeInterval.intersects(startsOnStartEndsAfter), is(true));

        TimeInterval startsInEndsIn = new TimeInterval(start.plusHours(1), end.minusHours(1));
        assertThat(timeInterval.intersects(startsInEndsIn), is(true));

        TimeInterval startsInEndsOn = new TimeInterval(start.plusHours(1), end);
        assertThat(timeInterval.intersects(startsInEndsOn), is(true));

        TimeInterval startsInEndsAfter = new TimeInterval(start.plusHours(1), end.plusHours(3));
        assertThat(timeInterval.intersects(startsInEndsAfter), is(true));

        TimeInterval startsOnEndEndsAfter = new TimeInterval(end, end.plusHours(1));
        assertThat(timeInterval.intersects(startsOnEndEndsAfter), is(false));

        TimeInterval startsAfterEndsAfter = new TimeInterval(start.plusHours(4), end.plusHours(4));
        assertThat(timeInterval.intersects(startsAfterEndsAfter), is(false));
    }

    @Test
    // TODO Should start and end be in Interval?
    public void timeInIntervalRight() {
        LocalDateTime before = start.minusHours(3);
        assertThat(timeInterval.timeInInterval(before), is(false));

//        LocalDateTime onStart = start;
//        assertThat(timeInterval.timeInInterval(onStart), is(true));

        LocalDateTime inInterval = start.plusHours(1);
        assertThat(timeInterval.timeInInterval(inInterval), is(true));

//        LocalDateTime onEnd = end;
//        assertThat(timeInterval.timeInInterval(onEnd), is(true));

        LocalDateTime after = end.plusHours(3);
        assertThat(timeInterval.timeInInterval(after), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenSetEndBeforeStart() {
        timeInterval.setEnd(end);
        timeInterval.setEnd(start.minusHours(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenSetStartAfterEnd() {
        timeInterval.setStart(start);
        timeInterval.setStart(end.plusHours(1));
    }

}
