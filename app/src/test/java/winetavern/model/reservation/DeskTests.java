package winetavern.model.reservation;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */

public class DeskTests extends AbstractWebIntegrationTests{

    @Test(expected = IllegalArgumentException.class)
    public void DeskContructorNegativeCapacityTest() {
        Desk desk = new Desk("1", -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DeskContructorNegativeNumberTest() {
        //Desk desk = new Desk(5,-3);
    }

}
