package winetavern.model.reservation;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */

public class TableTests extends AbstractWebIntegrationTests{

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeCapacityTest() {
        Table table = new Table(-2,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeNumberTest() {
        Table table = new Table(5,-3);
    }

}
