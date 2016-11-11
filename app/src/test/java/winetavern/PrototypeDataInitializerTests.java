package winetavern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.user.Roles;

import java.util.Optional;

/**
 * Tests for data initialization
 * @author Niklas Wünsche
 */

public class PrototypeDataInitializerTests extends AbstractWebIntegrationTests{

    @Autowired UserAccountManager userAccountManager;
    @Autowired EventCatalog eventCatalog;

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

}
