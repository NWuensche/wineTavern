package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.RequestHelper;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.stock.ProductCatalog;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class DayMenuItemManagerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private DayMenuRepository dayMenuRepository;
    @Autowired private ProductCatalog productCatalog;
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
        Product product = new Product("Product", Money.of(3, EURO));
        productCatalog.save(product);
        dayMenuItem.setProduct(product);

        RequestBuilder request = buildGetAdminRequest("/admin/menuitem/add")
                .param("frommenuitemid", "" + dayMenu.getId());

        mvc.perform(request)
            .andExpect(model().attributeExists("daymenuitems"))
            .andExpect(view().name("addmenuitem"));
    }

    @Test
    public void addNewItemRight() throws Exception {
        Product newProduct = new Product("Prod", Money.of(3, EURO));
        String nameOfMenuItem = "Beer";
        Long redirectToMenu = dayMenu.getId();

        RequestBuilder request = buildPostAdminRequest("/admin/menuitem/add")
                .param("product", newProduct.getId().toString())
                .param("name", nameOfMenuItem)
                .param("price", Money.of(3, EURO).toString())
                .param("description", "Awesome")
                .param("quantityPerProduct", "" + 4.5)
                .param("enabled", "" + false)
                .param("dayMenu", redirectToMenu.toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        List<DayMenuItem> storedDayMenuItems = dayMenuItemRepository
                .stream()
                .collect(Collectors.toList());

        assertThat(storedDayMenuItems.size(), is(2));
        assertThat(storedDayMenuItems.get(1).getName(), is(nameOfMenuItem));
    }

    @Test
    public void addExistingRight() throws Exception {
        DayMenuItem newItem = new DayMenuItem("new", "new", Money.of(3, EURO), 3.0);
        dayMenuItemRepository.save(newItem);

        RequestBuilder request = buildPostAdminRequest("/admin/menuitem/addExisting")
                .param("daymenuitem", newItem.getId().toString())
                .param("dayMenu", dayMenu.getId().toString());

        mvc.perform(request);

        assertThat(newItem.getDayMenus().contains(dayMenu), is(true));
        assertThat(dayMenu.getDayMenuItems().contains(newItem), is(true));
    }

    @Test
    public void deleteItemRight() throws Exception {
        dayMenu.addMenuItem(dayMenuItem);

        assertThat(dayMenuItem.getDayMenus().contains(dayMenu), is(true));
        assertThat(dayMenu.getDayMenuItems().contains(dayMenuItem), is(true));

        mvc.perform(RequestHelper.buildPostAdminRequest("/admin/menuitem/remove/"
                        .concat(dayMenu.getId().toString())
                        .concat("/")
                        .concat(dayMenuItem.getId().toString())));

        assertThat(dayMenuItem.getDayMenus().contains(dayMenu), is(false));
        assertThat(dayMenu.getDayMenuItems().contains(dayMenuItem), is(false));
    }

    @Test
    public void editMenuItemRightPost() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);

        DayMenu secoundMenu = new DayMenu(LocalDate.now());
        secoundMenu.addMenuItem(dayMenuItem);

        Product newProduct = new Product("product", Money.of(4,EURO));

        String url = "/admin/menuitem/edit/"
                .concat(dayMenu.getId().toString())
                .concat("/")
                .concat(dayMenuItem.getId().toString());

        RequestBuilder request = buildPostAdminRequest(url)
                .param("product", newProduct.toString())
                .param("name", "Neu")
                .param("price", Money.of(5, EURO).toString())
                .param("description", "New Desc")
                .param("quantityPerProduct", "" + 10)
                .param("enabled", new Boolean(true).toString());

        mvc.perform(request);

        assertTrue(dayMenuItemRepository
                .stream()
                .anyMatch(dItem -> dItem.getName().equals("Neu")));

        // Test if there is still the old item for the second dayMenu
        assertTrue(dayMenuItemRepository
                .stream()
                .anyMatch(dItem -> dItem.getName().equals("Coke")));

    }


    @Test
    public void errorWhenEditWithoutId() throws Exception{
        Product newProduct = new Product("product", Money.of(4,EURO));

        String url = "/admin/menuitem/edit/"
                .concat(dayMenu.getId().toString())
                .concat("/")
                .concat("1337101"); // Not existing DayMenuItem id

        RequestBuilder request = buildPostAdminRequest(url)
                .param("product", newProduct.toString())
                .param("name", "Neu")
                .param("price", Money.of(5, EURO).toString())
                .param("description", "New Desc")
                .param("quantityPerProduct", "" + 10)
                .param("enabled", new Boolean(true).toString());

        mvc.perform(request)
                .andExpect(view().name("error"));
    }

    @Test
    public void editMenuItemRightGet() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);

        String url = "/admin/menuitem/edit/"
                .concat(dayMenu.getId().toString())
                .concat("/")
                .concat(dayMenuItem.getId().toString());

        mvc.perform(RequestHelper.buildGetAdminRequest(url))
                .andExpect(view().name("editmenuitem"))
                .andExpect(model().attributeExists("menuitem"));
    }

    @Test
    public void throwEditMenuItemRightWithoutIdGet() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);

        String url = "/admin/menuitem/edit/"
                .concat(dayMenu.getId().toString())
                .concat("/")
                .concat("1337101"); // Not existing DayMenuItem id

        mvc.perform(RequestHelper.buildGetAdminRequest(url))
                .andExpect(view().name("error"));
    }

}
