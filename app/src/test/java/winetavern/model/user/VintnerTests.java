package winetavern.model.user;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author Niklas Wünsche
 */

public class VintnerTests {

    @Test(expected = NullPointerException.class)
    public void throwWhenVintnerWithNoName() {
        new Vintner(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenVintnerWithEmptyName() {
        new Vintner("", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenVintnerWithNegativePosition() {
        new Vintner("Vintner", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenVintnerSetNegativePosition() {
        new Vintner("Vintner", 1).setPosition(-1);
    }

    @Test
    public void toStringRight() {
        Vintner vintner = new Vintner("Vintner", 1);
        assertThat(vintner.toString(), is("Vintner"));
    }

}