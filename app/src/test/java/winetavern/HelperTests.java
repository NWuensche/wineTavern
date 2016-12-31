package winetavern;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

/**
 * @author Niklas Wünsche
 */
public class HelperTests {

    @Test
    public void isVintnerDayRight() {
        assertThat(Helper.isVintnerDay(LocalDate.of(2016, 11, 4)), is(true));

        assertThat(Helper.isVintnerDay(LocalDate.of(2016, 12, 4)), is(false));
    }

}