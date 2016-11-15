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
        Category redWine = Category.RED_WINE;

        assertThat(redWine.getCategoryName(), is("RED_WINE"));
        assertThat(Category.getDisplayNameCategory(Category.RED_WINE), is("Rotwein"));
    }

    @Test
    public void isSnackDataRight() {
        Category snack = Category.SNACK;

        assertThat(snack.getCategoryName(), is("SNACK"));
        assertThat(Category.getDisplayNameCategory(Category.SNACK), is("Snacks"));
    }

}
