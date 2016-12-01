package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.Helper;
import winetavern.model.menu.DayMenuItem;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Niklas Wünsche
 */

public class AccountancyIntegrationTests extends AbstractIntegrationTests {

    @Autowired private BillRepository bills;
    @Autowired private BillItemRepository billItems;

    private Bill bill = new Bill("B1", null);
    private DayMenuItem dayMenuItem1 = new DayMenuItem("Stuff", Money.of(4, EURO));
    private DayMenuItem dayMenuItem2 = new DayMenuItem("Tasty bacon", Money.of(5, EURO));
    private BillItem billItem1 = new BillItem(dayMenuItem1);
    private BillItem billItem2 = new BillItem(dayMenuItem2);

    @Before
    public void setup() {
        billItems.save(Arrays.asList(billItem1, billItem2));

        bill.changeItem(billItem1, 1);
        bill.changeItem(billItem2, 1);

        bills.save(bill);
    }

    @Test
    public void isPreconditionRight() {
        Bill firstStoredBill = Helper.getFirstItem(bills.findAll());

        assertThat(firstStoredBill, is(bill));
        assertThat(firstStoredBill.getItems().size(), is(2));
    }

    @Test
    public void saveBill() {
        Bill bill = new Bill("B1", null);
        bills.save(bill);
        assertThat(bills.findOne(bill.getId()).get(), is(bill));
    }

}
