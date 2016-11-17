package winetavern.model.reservation;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */

public class TableTests extends AbstractWebIntegrationTests{

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeCapacityTest() {
        Desk desk = new Desk("1", -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TableContructorNegativeNumberTest() {
        //Desk desk = new Desk(5,-3);
    }

}
