package winetavern;

import org.junit.Test;
import org.salespointframework.accountancy.Accountancy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Niklas Wünsche
 */
public class HelperWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired Accountancy accountancy;

    @Test
    public void isVintnerDayRight() {
        assertThat(Helper.isVintnerDay(LocalDate.of(2016, 11, 4)), is(true));

        assertThat(Helper.isVintnerDay(LocalDate.of(2016, 12, 4)), is(false));
    }

    @Test
    public void localDateTimeToTimeStringRight() {
        LocalDateTime time = LocalDateTime.of(2016, 11, 11, 11, 11);
        assertThat(Helper.localDateTimeToTimeString(time), is("11:11"));
    }

    @Test
    public void findNoneIfIdIsWrong() {
        assertThat(Helper.findOne("1293012", accountancy), is(nullValue()));
    }

}