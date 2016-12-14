package winetavern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.user.ExternalManager;
import winetavern.model.user.Roles;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Tests for data initialization
 * @author Niklas Wünsche
 */
public class WineTavernDataInitializerTests extends AbstractWebIntegrationTests{

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private EventCatalog eventCatalog;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ShiftRepository shifts;
    @Autowired private ExternalManager externalManager;

    @Test
    public void adminInDB() throws Exception {
        Optional<UserAccount> admin;

        mvc.perform(get("/"));
        admin = userAccountManager.findByUsername("admin");

        assertThat(admin.isPresent(), is(true));
        assertThat(admin.get().hasRole(Roles.ADMIN.getRole()), is(true));
    }

    @Test
    public void eventsInDB() throws Exception {
        Event event1, event2;

        mvc.perform(get("/"));
        event1 = eventCatalog.findByName("Go hard or go home - Ü80 Party").iterator().next();
        event2 = eventCatalog.findByName("Grillabend mit Musik von Barny dem Barden").iterator().next();

        assertThat(event1.getDescription(), is("SW4G ist ein muss!"));
        assertThat(event2.getDescription(), is("Es wird gegrillt und überteuerter Wein verkauft."));
    }

    @Test
    public void inventoryItemsInDB() throws Exception {
        String item1 = "Vodka";
        String item2 = "Berliner Brandstifter";
        Iterable<InventoryItem> items = stock.findAll();
        List<String> names = new LinkedList<>();

        items.forEach(item -> names.add(item.getProduct().getName()));

        mvc.perform(get("/"));

        assertThat(names.contains(item1), is(true));
        assertThat(names.contains(item2), is(true));
    }

    @Test
    public void shiftInDB() throws Exception {
        assertTrue(shifts.count() > 0);
    }

    @Test
    public void externalInDB() throws Exception {
        assertThat(externalManager.findByName("DJ Cool").isPresent(), is(true));
    }

}
