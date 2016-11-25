package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.menu.DayMenuItem;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

public class BillItemTests extends AbstractWebIntegrationTests {
    private DayMenuItem dayMenuItem = new DayMenuItem("Schinken", Money.of(3.56, EURO));
    private int quantity = 6;
    private BillItem billItem = new BillItem(dayMenuItem);

    @Before
    public void setup() {
        billItem.changeQuantity(quantity);
    }

    @Test
    public void getCorrectPrice() {
        Assert.assertEquals(dayMenuItem.getPrice().multiply(quantity), billItem.getPrice());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenNegativeQuantity() {
        billItem.changeQuantity(-1);
    }
}
