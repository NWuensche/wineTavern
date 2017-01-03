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
    private int quantity;
    private BillItem billItem;

    @Before
    public void setup() {
        quantity = 6;

        mockedDayMenuItem = mock(DayMenuItem.class);
        when(mockedDayMenuItem.getPrice()).thenReturn(Money.of(3.56, EURO));
        when(mockedDayMenuItem.getName()).thenReturn("Schinken");

        billItem = new BillItem(mockedDayMenuItem);
        billItem.changeQuantity(quantity);
    }

    @Test
    public void getCorrectPrice() {
        assertThat(billItem.getPrice(), is(mockedDayMenuItem.getPrice().multiply(quantity)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeQuantity() {
        billItem.changeQuantity(-1);
    }

    @Test
    public void testToString() {
        assertThat(billItem.toString(), is("6 x Schinken"));
    }

    @Test
    public void isReadyRight() {
        Given:
        assertThat(billItem.getReady(), is(false));

        When:
        billItem.ready();

        Then:
        assertThat(billItem.getReady(), is(true));
    }

}
