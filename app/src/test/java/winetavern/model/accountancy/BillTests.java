package winetavern.model.accountancy;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import winetavern.model.menu.DayMenuItem;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * @author Louis, Niklas
 */
public class BillTests {

    private Bill bill;
    private BillItem mockedBillItem1;
    private BillItem mockedBillItem2;
    private LocalDateTime time;

    @Before
    public void setup() {
        time = LocalDateTime.of(2016, 11, 11, 11, 11, 11);
        bill = new Bill("B1", null);

        mockedBillItem1 = mock(BillItem.class);
        mockedBillItem2 = mock(BillItem.class);
        when(mockedBillItem1.getPrice()).thenReturn(Money.of(1, EURO));
        when(mockedBillItem2.getPrice()).thenReturn(Money.of(2, EURO));

        bill.changeItem(mockedBillItem1, 1);
        bill.changeItem(mockedBillItem2, 1);
    }

    @Test
    public void getCorrectPrice() {
        MonetaryAmount addedPrices = Money.from(mockedBillItem1.getPrice()).add(mockedBillItem2.getPrice());

        assertThat(bill.getPrice(), is(addedPrices));
    }

    @Test(expected = IllegalStateException.class)
    public void throwOnChangeIfClosed() {
        BusinessTime mockedBusinessTime = getMockedBusinessTime();
        bill.close(mockedBusinessTime);

        bill.changeItem(mockedBillItem1, 3);
    }

    private BusinessTime getMockedBusinessTime() {
        BusinessTime mockedBusinessTime = mock(BusinessTime.class);
        when(mockedBusinessTime.getTime()).thenReturn(time);

        return mockedBusinessTime;
    }

    @Test(expected = NullPointerException.class)
    public void throwOnChangeIfItemIsNull() {
        bill.changeItem(null, 3);
    }

    @Test
    public void removeItem() {
        bill.changeItem(mockedBillItem1, 0);
        assertThat(bill.getItems().contains(mockedBillItem1), is(false));
    }

    @Test
    public void addItem() {
        DayMenuItem mockedItem = mock(DayMenuItem.class);
        BillItem billItem3 = new BillItem(mockedItem);

        bill.changeItem(billItem3, 4);

        assertThat(bill.getItems().contains(billItem3), is(true));
    }

    @Test
    public void dontAddEmptyNewItem() {
        BillItem dontAdd = mock(BillItem.class);
        bill.changeItem(dontAdd, 0);

        assertThat(bill.getItems().contains(dontAdd), is(false));
    }

    @Test
    public void rightCloseTime() {
        BusinessTime mockedBusinessTime = getMockedBusinessTime();
        bill.close(mockedBusinessTime);

        assertThat(bill.getCloseTime(), is(time));
    }

}
