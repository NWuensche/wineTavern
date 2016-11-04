package winetavern.model.reservation;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

/**
 * @author Sev
 */
public class TableTests extends AbstractWebIntegrationTests{

    @Test(expected = InvalidArgumentException.class)
    public void TableContructorNegativeCapacityTest() throws Exception{
        Table table = new Table(-2,1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void TableContructorNegativeNumberTest() throws Exception{
        Table table = new Table(5,-3);
    }
}
