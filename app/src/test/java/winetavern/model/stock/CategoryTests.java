package winetavern.model.stock;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;


/**
 * Created by nwuensche on 14.11.16.
 */
public class CategoryTests {

    @Test
    public void isRedWineDataRight() {
        assertThat(Category.RED_WINE.toString(), is("Rotwein"));
    }

    @Test
    public void isSnackDataRight() {
        assertThat(Category.SNACK.toString(), is("Snacks"));
    }

}
