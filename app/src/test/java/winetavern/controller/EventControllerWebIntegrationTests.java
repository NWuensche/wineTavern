package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import winetavern.model.user.ExternalManager;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static winetavern.controller.RequestHelper.buildGetAdminRequest;
import static winetavern.controller.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class EventControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private EventCatalog eventCatalog;
    @Autowired private ExternalManager externalManager;
    @Autowired VintnerManager vintnerManager;
    private Event event;
    private Vintner vintner;

    @Before
    public void before() {
        eventCatalog.deleteAll();
        vintnerManager.deleteAll();
        externalManager.deleteAll();

        vintner = new Vintner("test", 2);
        vintnerManager.save(vintner);
        TimeInterval timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        event = new Event("Event", Money.of(3, EURO), timeInterval, "Descritpion", vintner);
        eventCatalog.save(event);
    }

    private void addVintnerDays() {
        try {
            mvc.perform(buildPostAdminRequest("/admin/events"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void manageEventsRightWhenVintnerExisits() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/events"))
                .andExpect(model().attribute("eventAmount", is(eventCatalog.count())))
                .andExpect(model().attributeExists("calendarString"))
                .andExpect(view().name("events"));
    }

    @Test
    public void manageEventsRightWhenVintnersEmpty() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/events"))
                .andExpect(model().attribute("eventAmount", is(eventCatalog.count())))
                .andExpect(model().attributeExists("calendarString"))
                .andExpect(view().name("events"));
    }

    @Test
    public void addEventPostRight() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "New");
        params.put("desc", "Desc");
        params.put("date", "11.11.2016 11:11 - 11.11.2016 23:11");
        params.put("price", "6.66");
        params.put("external", "" + event.getPerson().getId());
        params.put("externalName", "t");
        params.put("externalWage", "t");

        mvc.perform(buildPostAdminRequest("/admin/events/add", params))
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(2l));
        assertTrue(eventCatalog
                .stream()
                .anyMatch(event -> event.getName().equals("New")));
    }


    @Test
    public void addEventGetRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/admin/events/add"))
                .andExpect(model().attributeExists("externals"))
                .andExpect(view().name("addevent"));
    }


    @Test
    public void removeEventRight() throws Exception {
        assertThat(eventCatalog.count(), is(1l));

        mvc.perform(buildGetAdminRequest("/admin/events/remove/" + event.getId()))
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(0l));
    }

    @Test
    public void changeEventGetRight() throws Exception {
        addVintnerDays();

        mvc.perform(buildGetAdminRequest("/admin/events/change/" + event.getId()))
                .andExpect(model().attributeExists("event"))
                .andExpect(view().name("events"));
    }

    @Test
    public void changeEventPostRight() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "new");
        params.put("desc", "new");
        params.put("date", "11.11.2015 11:11 - 11.11.2016 11:11");
        params.put("price", "6.50");
        params.put("external", vintner.getId().toString());
        params.put("externalName", "Hugo");
        params.put("externalWage", "100.0");

        mvc.perform(buildPostAdminRequest("/admin/events/change/" + event.getId(), params))
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.findOne(event.getId()).map(Event::getName), is(Optional.of("new")));
    }

    @Test
    public void showVintnerRightWithNoQuery() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/events/vintner/"))
                .andExpect(model().attributeExists("missing"))
                .andExpect(view().name("vintner"));
    }

    @Test
    public void showVintnerRightWithEmptyQuery() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("query", "");

        mvc.perform(buildPostAdminRequest("/admin/events/vintner", params))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void showVintnerRightWithQuery() throws Exception {
        Vintner newVintner = new Vintner("new", 3);
        vintnerManager.save(newVintner);

        HashMap<String, String> params = new HashMap<>();
        params.put("query", vintner.getName().concat("|").concat("newSavedVintner"));

        mvc.perform(buildPostAdminRequest("/admin/events/vintner", params))
                .andExpect(status().is3xxRedirection());

        assertThat(vintner.isActive(), is(true));
        assertThat(newVintner.isActive(), is(false));
        assertThat(vintnerManager.findByName("newSavedVintner").isPresent(), is(true));
    }

}
