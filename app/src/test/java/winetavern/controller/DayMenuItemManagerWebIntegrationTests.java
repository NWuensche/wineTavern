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
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.Arrays;
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
import static winetavern.controller.RequestHelper.buildGetAdminRequest;
import static winetavern.controller.RequestHelper.buildPostAdminRequest;

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

        HashMap<String, String> params = new HashMap<>();
        params.put("frommenuitemid", "" + dayMenu.getId());

        mvc.perform(buildGetAdminRequest("/admin/menuitem/add", params))
            .andExpect(model().attributeExists("daymenuitems"))
            .andExpect(view().name("addmenuitem"));
    }

    @Test
    public void addNewItemRight() throws Exception {
        Product newProduct = new Product("Prod", Money.of(3, EURO));
        String nameOfMenuItem = "Beer";
        Long redirectToMenu = dayMenu.getId();

        HashMap<String, String> params = new HashMap<>();
        params.put("product", newProduct.getId().toString());
        params.put("name", nameOfMenuItem);
        params.put("price", Money.of(3, EURO).toString());
        params.put("description", "Awesome");
        params.put("quantityPerProduct", "" + 4.5);
        params.put("enabled", "" + false);
        params.put("dayMenu", redirectToMenu.toString());

        mvc.perform(buildPostAdminRequest("/admin/menuitem/add", params))
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

        HashMap<String, String> params = new HashMap<>();
        params.put("daymenuitem", newItem.getId().toString());
        params.put("dayMenu", dayMenu.getId().toString());

        mvc.perform(RequestHelper.buildPostAdminRequest("/admin/menuitem/addExisting", params));

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

        HashMap<String, String> params = new HashMap<>();
        params.put("product", newProduct.toString());
        params.put("name", "Neu");
        params.put("price", Money.of(5, EURO).toString());
        params.put("description", "New Desc");
        params.put("quantityPerProduct", "" + 10);
        params.put("enabled", new Boolean(true).toString());

        mvc.perform(RequestHelper.buildPostAdminRequest(url, params));

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

        HashMap<String, String> params = new HashMap<>();
        params.put("product", newProduct.toString());
        params.put("name", "Neu");
        params.put("price", Money.of(5, EURO).toString());
        params.put("description", "New Desc");
        params.put("quantityPerProduct", "" + 10);
        params.put("enabled", new Boolean(true).toString());

        mvc.perform(RequestHelper.buildPostAdminRequest(url, params))
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
