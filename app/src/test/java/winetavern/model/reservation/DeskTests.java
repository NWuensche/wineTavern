package winetavern.model.reservation;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */

public class DeskTests extends AbstractWebIntegrationTests{

    @Test(expected = IllegalArgumentException.class)
    public void DeskConstructorNegativeCapacityTest() {
        Desk desk = new Desk("1", -2);
    }

}
