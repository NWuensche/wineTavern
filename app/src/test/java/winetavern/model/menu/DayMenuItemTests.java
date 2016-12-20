package winetavern.model.menu;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.mockito.Mockito.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import winetavern.Helper;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Niklas Wünsche
 */
public class DayMenuItemTests {

    private DayMenuItem dayMenuItem;

    @Before
    public void before() {
        dayMenuItem = new DayMenuItem("Name", "Description", Money.of(3, EURO), 6.0);
    }

    @Test
    public void addRight() {
        addDayMenus();
        assertThat(dayMenuItem.getDayMenus().size(), is(2));
    }

    private DayMenu addDayMenus() {
        DayMenu mockedDayMenu = mock(DayMenu.class);
        DayMenu mockedDayMenu2 = mock(DayMenu.class);
        //dayMenuItem.addDayMenu(mockedDayMenu);
        //dayMenuItem.addDayMenu(mockedDayMenu2);

        return mockedDayMenu;
    }

    @Test
    public void removeRight() {
        DayMenu mockedDayMenu = addDayMenus();

        dayMenuItem.removeDayMenu(mockedDayMenu);

        assertThat(dayMenuItem.getDayMenus().size(), is(1));
        assertThat(dayMenuItem.getDayMenus().contains(mockedDayMenu), is(false));
    }

    @Test
    public void cloneRight() {
        DayMenu mockedDayMenu = mock(DayMenu.class);
        DayMenuItem clonedItem = dayMenuItem.clone(mockedDayMenu);
        assertThat(clonedItem, is(not(dayMenuItem)));
        assertThat(Helper.getFirstItem(clonedItem.getDayMenus()), is(mockedDayMenu));
    }

}
