package winetavern.model.menu;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

/**
 * @author Niklas Wünsche
 */

public class DayMenuTests {

    // TODO PreRemove?
    private DayMenu dayMenu;

    @Before
    public void before() {
        Calendar calendar = Calendar.getInstance();
        dayMenu = new DayMenu(calendar);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenCalendarNull() {
        new DayMenu(null);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenSetCalendarNull() {
        dayMenu.setDay(null);
    }

    @Test
    public void getReadableDayRight() {
        assertThat(dayMenu.getReadableDay(), is(LocalDate.now().toString()));
    }

    @Test
    public void storeDayMenusRight() {
        storeItemsInDayMenu();

        assertThat(dayMenu.getDayMenuItems().size(), is(2));
    }

    private DayMenuItem storeItemsInDayMenu() {
        DayMenuItem mockedItem1 = mock(DayMenuItem.class);
        DayMenuItem mockedItem2 = mock(DayMenuItem.class);

        dayMenu.addMenuItem(mockedItem1);
        dayMenu.addMenuItem(mockedItem2);

        return mockedItem1;
    }

    @Test
    public void removeItemRight() {
        DayMenuItem mockedItem = storeItemsInDayMenu();

        dayMenu.removeMenuItem(mockedItem);

        assertThat(dayMenu.getDayMenuItems().contains(dayMenu), is(false));
        assertThat(dayMenu.getDayMenuItems().size(), is(1));
    }

}
