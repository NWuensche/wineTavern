package winetavern.model.accountancy;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import winetavern.model.menu.DayMenuItem;


/**
 * @author Louis
 */

public class BillItemTests {

    private DayMenuItem dayMenuItem = new DayMenuItem("Schinken", Money.of(3.56, EURO));
    private int quantity = 6;
    private BillItem billItem = new BillItem(dayMenuItem);

    @Before
    public void setup() {
        billItem.changeQuantity(quantity);
    }

    @Test
    public void getCorrectPrice() {
        assertEquals(dayMenuItem.getPrice().multiply(quantity), billItem.getPrice());
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
