package winetavern.model.user;

import org.junit.Test;
import org.salespointframework.catalog.Product;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
    public void attributesRight() {
        Vintner vintner = new Vintner("Vintner", 1);

        assertThat(vintner.toString(), is("Vintner"));
        assertThat(vintner.getPosition(), is(1));
    }

    @Test
    public void removeWineRight() {
        Product mockedWine = mock(Product.class);
        Vintner vintner = new Vintner("Vintner", 1);
        vintner.addWine(mockedWine);

        assertThat(vintner.removeWine(mockedWine), is(true));
    }

}