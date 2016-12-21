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
public class DayMenuItemManagerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private DayMenuItemManager dayMenuItemManager;
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

        RequestBuilder request = get("/admin/menuitem/add").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("frommenuitemid", "" + dayMenu.getId());

        mvc.perform(request)
            .andExpect(model().attributeExists("daymenuitems"))
            .andExpect(view().name("addmenuitem"));
    }

    @Test
    public void getNotAddedDayMenuItemsRight() {
        dayMenu.addMenuItem(dayMenuItem);

        DayMenuItem notInMenu = new DayMenuItem("Pepse", "Awesome", Money.of(2, EURO), 3.0);

        Iterable<DayMenuItem> dayMenuItems = Arrays.asList(dayMenuItem, notInMenu);
        List<DayMenuItem> notAdded = dayMenuItemManager.getNotAddedDayMenuItems(dayMenuItems, dayMenu);

        assertThat(notAdded.size(),is(1));
        assertThat(notAdded.get(0), is(notInMenu));
    }

    @Test
    //TODO Should be DayMenu(Item) Test
    public void addDayMenuToItemRight() {
        DayMenuItem newItem = new DayMenuItem("Pepse", "Awesome", Money.of(2, EURO), 3.0);
        dayMenu.addMenuItem(newItem);

        assertThat(dayMenu.getDayMenuItems().contains(newItem), is(true));
        assertThat(newItem.getDayMenus().contains(dayMenu), is(true));
    }

    //TODO Test Remove alot
    //TODO Should be DayMenu(Item) Test
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

    @Test
    public void addExistingRight() throws Exception {
        DayMenuItem newItem = new DayMenuItem("new", "new", Money.of(3, EURO), 3.0);
        dayMenuItemRepository.save(newItem);

        RequestBuilder request = post("/admin/menuitem/addExisting")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("daymenuitem", newItem.getId().toString())
                .param("dayMenu", dayMenu.getId().toString());

        mvc.perform(request);

        assertThat(newItem.getDayMenus().contains(dayMenu), is(true));
      //  assertThat(dayMenu.contains(newItem), is(true));
    }

    @Test
    public void deleteItemRight() throws Exception {
        //dayMenuItem.addDayMenu(dayMenu);

        RequestBuilder request = post("/admin/menuitem/remove/"+
                dayMenu.getId().toString() + "/" +
                dayMenuItem.getId().toString())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request);

        assertThat(dayMenuItem.getDayMenus().contains(dayMenu), is(false));
        //  assertThat(dayMenu.contains(dayMenuItem), is(false));
    }

    @Test
    public void editMenuItemRightPost() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);
        Product newProduct = new Product("product", Money.of(4,EURO));

        RequestBuilder request = post("/admin/menuitem/edit/"+
                dayMenu.getId().toString() + "/" +
                dayMenuItem.getId().toString())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("product", newProduct.toString())
                .param("name", "Neu")
                .param("price", Money.of(5, EURO).toString())
                .param("description", "New Desc")
                .param("quantityPerProduct", "" + 10)
                .param("enabled", new Boolean(true).toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(dayMenuItemRepository.findOne(dayMenuItem.getId()).isPresent(), is(true));
        assertThat(dayMenuItemRepository.findOne(dayMenuItem.getId()).get().getName(), is("Neu"));
    }

    @Test
    public void editMenuItemRightGet() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);

        RequestBuilder request = get("/admin/menuitem/edit/"+
                dayMenu.getId().toString() + "/" +
                dayMenuItem.getId().toString())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(view().name("editmenuitem"))
                .andExpect(model().attributeExists("menuitem"));
    }

    @Test
    public void throwEditMenuItemRightWithoutIdGet() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);

        RequestBuilder request = get("/admin/menuitem/edit/"+
                dayMenu.getId().toString() + "/" + "89023423091283")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(view().name("error")); // TODO Cant find error.html
    }

    @Test
    public void throwWhenEditWithoutId() throws Exception{
        dayMenu.addMenuItem(dayMenuItem);
        Product newProduct = new Product("product", Money.of(4,EURO));

        RequestBuilder request = post("/admin/menuitem/edit/"+
                dayMenu.getId().toString() + "/829304829340" )
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("product", newProduct.toString())
                .param("name", "Neu")
                .param("price", Money.of(5, EURO).toString())
                .param("description", "New Desc")
                .param("quantityPerProduct", "" + 10)
                .param("enabled", new Boolean(true).toString());

        mvc.perform(request)
                .andExpect(view().name("error")); // TODO Cant find error.html
    }

}
