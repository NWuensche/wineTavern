package winetavern.model.accountancy;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import winetavern.model.menu.DayMenuItem;


/**
 * @author Louis
 */

public class BillItemTests {

    private DayMenuItem mockedDayMenuItem;
    private int quantity = 6;
    private BillItem billItem;

    @Before
    public void setup() {
        mockedDayMenuItem = mock(DayMenuItem.class);
        when(mockedDayMenuItem.getPrice()).thenReturn(Money.of(3.56, EURO));
        when(mockedDayMenuItem.getName()).thenReturn("Schinken");

        billItem = new BillItem(mockedDayMenuItem);
        billItem.changeQuantity(quantity);
    }

    // TODO Should this be here?
    @Test
    public void getCorrectPrice() {
        assertEquals(mockedDayMenuItem.getPrice().multiply(quantity), billItem.getPrice());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeQuantity() {
        billItem.changeQuantity(-1);
    }

    @Test
    public void testToString() {
        assertThat(billItem.toString(), is("6 x Schinken"));
    }

}
