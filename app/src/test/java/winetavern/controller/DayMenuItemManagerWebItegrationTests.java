package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */

public class DayMenuItemManagerWebItegrationTests extends AbstractWebIntegrationTests {

    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private DayMenuItemManager dayMenuItemManager;
    @Autowired private DayMenuRepository dayMenuRepository;
    private DayMenuItem dayMenuItem;
    private DayMenu dayMenu;

    @Before
    public void before() {
        dayMenuItemRepository.deleteAll();
        dayMenuItem = new DayMenuItem("Coke", "Tasty", Money.of(3, EURO), 4.0);
        dayMenuItemRepository.save(dayMenuItem);

        dayMenu = new DayMenu(LocalDate.now());
        dayMenuRepository.save(dayMenu);
    }

    @Test
    public void addModelAttributesRight() throws Exception {
        RequestBuilder request = get("/admin/menuitem/add").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("camefrom", "" + dayMenuItem.getId());

        mvc.perform(request)
            .andExpect(model().attributeExists("daymenuitems"))
            .andExpect(view().name("addmenuitem"));
    }

    @Test
    public void getNotAddedDayMenuItemsRight() {
        DayMenuItem notInMenu = new DayMenuItem("Pepse", "Awesome", Money.of(2, EURO), 3.0);

        dayMenu.addMenuItem(dayMenuItem);

        Iterable<DayMenuItem> dayMenuItems = Arrays.asList(dayMenuItem, notInMenu);

        List<DayMenuItem> notAdded = dayMenuItemManager.getNotAddedDayMenuItems(dayMenuItems, dayMenu);

        assertArrayEquals(notAdded.toArray(), Arrays.asList(notInMenu).toArray());
    }

    @Test
    public void addNewItemRight() throws Exception {
        Product newProduct = new Product("Prod", Money.of(3, EURO));

        String nameOfMenuItem = "Beer";
        Money costOfMenuItem = Money.of(3, EURO);
        String descriptionOfMenuItem = "Awesome";
        Double quantityOfMenuItem = 4.5;
        boolean statusOfMenuItem = false;
        Long redirectToMenu = dayMenu.getId();

        RequestBuilder request = post("/admin/menuitem/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("product", newProduct.getId().toString())
                .param("name", nameOfMenuItem)
                .param("price", costOfMenuItem.toString())
                .param("description", descriptionOfMenuItem)
                .param("quantityPerProduct", quantityOfMenuItem.toString())
                .param("enabled", "" + statusOfMenuItem)
                .param("dayMenu", redirectToMenu.toString());


        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        List<DayMenuItem> storedDayMenuItems = Helper.convertToList(dayMenuItemRepository.findAll());

        assertThat(storedDayMenuItems.size(), is(2));
        assertThat(storedDayMenuItems.get(1).getName(), is(nameOfMenuItem));
    }


}
