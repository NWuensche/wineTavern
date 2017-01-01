package winetavern.controller;

import static org.mockito.Mockito.mock;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.util.NestedServletException;
import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.RequestHelper;
import winetavern.model.management.EventCatalog;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.stock.Category;
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Vintner;
import winetavern.model.user.VintnerManager;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Michel, Niklas
 */
public class DayMenuManagerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private ProductCatalog productCatalog;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private DayMenuRepository dayMenuRepository;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private VintnerManager vintnerManager;
    @Autowired private EventCatalog eventCatalog;

    private DayMenu dayMenu;
    private DayMenuItem dayMenuItem;
    private Vintner vintner;

    @Before
    public void before() {
        eventCatalog.deleteAll();
        Product prod = new Product("Prod", Money.of(3, EURO));
        prod.addCategory(Category.MENU.toString());
        productCatalog.save(prod);

        InventoryItem iItem = new InventoryItem(prod, Quantity.of(3.0));
        stock.save(iItem);

        vintner = new Vintner("test", 2);
        vintnerManager.save(vintner);
        addVintnerDays();

        dayMenuItem = new DayMenuItem("Name", "Desc", Money.of(3, EURO), 4.0);
        dayMenuItem.setProduct(prod);
        dayMenuItemRepository.save(dayMenuItem);

        dayMenu = new DayMenu(LocalDate.of(2016,11,11));
        dayMenu.addMenuItem(dayMenuItem);
        dayMenuRepository.save(dayMenu);
    }

    private void addVintnerDays() {
        try {
            mvc.perform(buildPostAdminRequest("/admin/events"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showMenusRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/menu/show"))
                .andExpect(model().attributeExists("menus"))
                .andExpect(view().name("daymenulist"));
    }

    @Test
    public void addMenuGetRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/admin/menu/add"))
                .andExpect(view().name("addmenu"));
    }

    @Test
    public void createDayMenuWithWrongYear() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "30.11.wrong");

        try {
            mvc.perform(request);
            fail();
        }
        catch (NestedServletException e) {
            Throwable causedThrow = e.getCause();
            assertThat(causedThrow.getMessage(), is("Text '30.11.wrong' could not be parsed at index 6"));
        }
    }

    @Test
    public void createDayMenuWithRealDateWithoutPreDayMenu() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "04.09.2015");

        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(2015, 9, 4);

        assertTrue(dayMenuRepository
                .stream()
                .anyMatch(date -> date.getDay().equals(givenDate)));
    }

    @Test
    public void createDayMenuWithRealDateWithPreDayMenu() throws Exception {
        DayMenu preDayMenu = new DayMenu(LocalDate.of(2015, 9, 3));
        dayMenuRepository.save(preDayMenu);

        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "04.09.2015");

        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(2015, 9, 4);

        assertTrue(dayMenuRepository
                .stream()
                .anyMatch(date -> date.getDay().equals(givenDate)));
    }

    @Test
    public void createDayMenuWithWineInVintner() throws Exception {
        Product wine = new Product("wine", Money.of(3, EURO));
        productCatalog.save(wine);

        DayMenuItem item = new DayMenuItem("test", "desc", Money.of(3, EURO), 3.4);
        item.setProduct(wine);
        dayMenuItemRepository.save(item);

        vintner.addWine(wine);
        vintnerManager.save(vintner);

        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "04.09.2015");

        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(2015, 9, 4);

        assertTrue(dayMenuRepository
                .stream()
                .anyMatch(date -> date.getDay().equals(givenDate)));
    }

    @Test
    public void createDayMenuWithoutEvents() throws Exception {
        eventCatalog.deleteAll();

        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "04.09.2015");

        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(2015, 9, 4);

        assertTrue(dayMenuRepository
                .stream()
                .anyMatch(date -> date.getDay().equals(givenDate)));
    }

    @Test
    public void createNoNewMenuIfThereIsOneForDate() throws Exception {
        createDayMenuWithWineInVintner();

        RequestBuilder request = buildPostAdminRequest("/admin/menu/add")
                .param("date", "04.09.2015");

        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(2015, 9, 4);

        assertTrue(dayMenuRepository
                .stream()
                .anyMatch(date -> date.getDay().equals(givenDate)));
    }

    @Test
    public void removeDayMenuRight() throws Exception {
        DayMenu willBeDeleted = new DayMenu(LocalDate.now());
        dayMenuRepository.save(willBeDeleted);

        RequestBuilder request = buildPostAdminRequest("/admin/menu/remove")
                .param("daymenuid", willBeDeleted.getId().toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(dayMenuRepository.findOne(willBeDeleted.getId()), is(Optional.empty()));
    }

    @Test
    public void editRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/menu/edit/" + dayMenu.getId()))
                .andExpect(view().name("editdaymenu"))
                .andExpect(model().attribute("daymenu", dayMenu))
                .andExpect(model().attributeExists("stock"));
    }

    @Test
    public void pdfRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/menu/print/" + dayMenu.getId()))
                .andExpect(view().name("daymenupdf"));
    }

    @Test
    public void pdfCatchRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/menu/print/13366040")); //wrong id
    }

}
