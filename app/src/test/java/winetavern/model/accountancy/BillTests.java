package winetavern.model.accountancy;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import winetavern.AbstractIntegrationTests;
import winetavern.model.menu.DayMenuItem;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * @author Louis
 */

public class BillTests extends AbstractIntegrationTests {


    private Bill bill = new Bill("B1", null);
    private DayMenuItem dayMenuItem1 = new DayMenuItem("Stuff", Money.of(4, EURO));
    private DayMenuItem dayMenuItem2 = new DayMenuItem("Tasty bacon", Money.of(5, EURO));
    private BillItem billItem1 = new BillItem(dayMenuItem1);
    private BillItem billItem2 = new BillItem(dayMenuItem2);

    @Before
    public void setup() {
        bill.changeItem(billItem1, 1);
        bill.changeItem(billItem2, 1);
    }

    @Test
    public void getCorrectPrice() {
        MonetaryAmount addedPrices = Money.from(dayMenuItem1.getPrice());
        addedPrices.add(dayMenuItem2.getPrice());
        assertEquals(addedPrices, bill.getPrice());
    }

    @Test(expected = IllegalStateException.class)
    public void throwOnChangeIfClosed() {
        LocalDateTime time = LocalDateTime.of(2016, 11, 11, 11, 11, 11);
        BusinessTime mockedBusinessTime = mock(BusinessTime.class);
        when(mockedBusinessTime.getTime()).thenReturn(time);

        bill.close(mockedBusinessTime);
        bill.changeItem(billItem1, 3);
    }

    @Test(expected = NullPointerException.class)
    public void throwOnChangeIfItemIsNull() {
        bill.changeItem(null, 3);
    }

    @Test
    public void removeItem() {
        bill.changeItem(billItem1, 0);
        assertThat(bill.getItems().contains(billItem1), is(false));
    }

    @Test
    public void addItem() {
        DayMenuItem dayMenuItem3 = new DayMenuItem("Tasty bacon", Money.of(5, EURO));
        BillItem billItem3 = new BillItem(dayMenuItem3);

        bill.changeItem(billItem3, 4);

        assertThat(bill.getItems().contains(billItem3), is(true));
    }

    @Test
    public void dontAddEmptyNewItem() {
        DayMenuItem dayMenuItem4 = new DayMenuItem("Tasty bacon", Money.of(5, EURO));
        BillItem billItem4 = new BillItem(dayMenuItem4);
        bill.changeItem(billItem4, 0);

        assertThat(bill.getItems().contains(billItem4), is(false));
    }

    @Test
    public void rightCloseTime() {
        LocalDateTime time = LocalDateTime.of(2016, 11, 11, 11, 11, 11);
        BusinessTime mockedBusinessTime = mock(BusinessTime.class);
        when(mockedBusinessTime.getTime()).thenReturn(time);
        bill.close(mockedBusinessTime);

        assertThat(bill.getCloseTime(), is(time));
    }

}
