package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.menu.DayMenuItem;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Transactional
public class BillTests extends AbstractWebIntegrationTests {
    @Autowired private BillRepository bills;
    private Bill bill = new Bill("B1", null);
    private DayMenuItem dayMenuItem1 = new DayMenuItem("Stuff", Money.of(4, EURO));
    private DayMenuItem dayMenuItem2 = new DayMenuItem("Tasty bacon", Money.of(5, EURO));
    private BillItem billItem1 = new BillItem(dayMenuItem1);
    private BillItem billItem2 = new BillItem(dayMenuItem2);

    @Before
    public void setup() {
        bills.save(bill);
        bill.changeItem(billItem1, 1);
        bills.save(bill);
        bill.changeItem(billItem2, 1);
        bills.save(bill);
    }

    @Test
    public void saveBill() {
        Bill bill = new Bill("B1", null);
        bills.save(bill);
        Assert.assertSame(bill, bills.findOne(bill.getId()).get());
    }

    @Test
    public void getCorrectPrice() {
        Assert.assertEquals(billItem1.getPrice().add(billItem2.getPrice()), bill.getPrice());
    }
}
