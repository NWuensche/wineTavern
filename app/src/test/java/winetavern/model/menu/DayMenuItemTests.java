package winetavern.model.menu;

import org.javamoney.moneta.Money;
import org.junit.Test;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Niklas Wünsche
 */

public class DayMenuItemTests {

    @Test
    public void createItemWorks() {
        new DayMenuItem("Name", "Description", Money.of(3, EURO), 6.0);
    }

}
