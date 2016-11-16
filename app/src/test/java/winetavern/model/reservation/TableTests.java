package winetavern.model.reservation;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */

public class TableTests extends AbstractWebIntegrationTests{

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeCapacityTest() {
        Table table = new Table("1", -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeNumberTest() {
        //Table table = new Table(5,-3);
    }

}
