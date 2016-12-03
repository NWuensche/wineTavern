package winetavern.model.management;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import winetavern.model.user.Employee;

import java.time.LocalDateTime;

/**
 * @author Niklas Wünsche
 */

public class ShiftTests {

    private TimeInterval mockedEarlyTimeInterval;
    private TimeInterval mockedLateTimeInterval;
    private Employee mockedEmployee;

    @Before
    public void before() {
        mockedEarlyTimeInterval = mock(TimeInterval.class);
        mockedLateTimeInterval = mock(TimeInterval.class);
        mockedEmployee = mock(Employee.class);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenIntervalNull() {
        new Shift(null, mockedEmployee);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenEmployeeNull() {
        new Shift(mockedEarlyTimeInterval, null);
    }

    @Test
    public void compareRight() {
        when(mockedEarlyTimeInterval.getStart()).thenReturn(LocalDateTime.now());
        when(mockedLateTimeInterval.getStart()).thenReturn(LocalDateTime.now().plusHours(3));

        Shift earlyShift = new Shift(mockedEarlyTimeInterval, mockedEmployee);
        Shift lateShift = new Shift(mockedLateTimeInterval, mockedEmployee);

        assertThat(earlyShift.compareTo(lateShift), is(lessThan(0)));
    }

}
