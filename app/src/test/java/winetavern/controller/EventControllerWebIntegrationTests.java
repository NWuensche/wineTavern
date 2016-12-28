package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        RequestBuilder request = post("/admin/events")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        try {
            mvc.perform(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void manageEventsRightWhenVintnerExisits() throws Exception {
        RequestBuilder request = post("/admin/events")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attribute("eventAmount", is(eventCatalog.count())))
                .andExpect(model().attributeExists("calendarString"))
                .andExpect(view().name("events"));
    }

    @Test
    public void manageEventsRightWhenVintnersEmpty() throws Exception {
        RequestBuilder request = post("/admin/events")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attribute("eventAmount", is(eventCatalog.count())))
                .andExpect(model().attributeExists("calendarString"))
                .andExpect(view().name("events"));
    }

    @Test
    public void addEventPostRight() throws Exception {
        RequestBuilder request = post("/admin/events/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("name", event.getName())
                .param("desc", event.getDescription())
                .param("date", Helper.localDateTimeToDateTimeString(event.getInterval().getStart()) + " - " +
                               Helper.localDateTimeToDateTimeString(event.getInterval().getEnd()))
                .param("price", event.getPrice().getNumber().doubleValue() + "")
                .param("external", event.getPerson().getId() + "")
                .param("externalName", "")
                .param("externalWage", "");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(2l));

        Event storedEvent = Helper.getFirstItem(eventCatalog.findByName(event.getName()));

        assertThat(storedEvent.compareTo(event), is(0));
    }

    @Test
    public void addEventGetRight() throws Exception {
        RequestBuilder request = get("/admin/events/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("externals"))
                .andExpect(view().name("addevent"));
    }


    @Test
    public void removeEventRight() throws Exception {
        Given:
        assertThat(eventCatalog.count(), is(1l));
        RequestBuilder request = get("/admin/events/remove/" + event.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        When:
        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        Then:
        assertThat(eventCatalog.count(), is(0l));
    }


    @Test
    public void changeEventGetRight() throws Exception {
        addVintnerDays();

        RequestBuilder request = get("/admin/events/change/" + event.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("event"))
                .andExpect(view().name("events"));
    }

    @Test
    public void changeEventPostRight() throws Exception {
        RequestBuilder request = post("/admin/events/change/" + event.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("name", "new")
                .param("desc", "new")
                .param("date", "11.11.2015 11:11 - 11.11.2016 11:11")
                .param("price", "6.50")
                .param("external", vintner.getId().toString())
                .param("externalName", "Hugo") // TODO wird das in view richtig gemacht?
                .param("externalWage", "100.0");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.findOne(event.getId()).map(Event::getName), is(Optional.of("new")));
    }

    @Test
    public void showVintnerRightWithNoQuery() throws Exception {
        RequestBuilder request = post("/admin/events/vintner/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("missing"))
                .andExpect(view().name("vintner"));

    }

    @Test
    public void showVintnerRightWithEmptyQuery() throws Exception {
        RequestBuilder request = post("/admin/events/vintner/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("query", "");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void showVintnerRightWithQuery() throws Exception {
        Vintner newVintner = new Vintner("new", 3);
        vintnerManager.save(newVintner);
        RequestBuilder request = post("/admin/events/vintner/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("query", vintner.getName().concat("|").concat("newSavedVintner"));

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(vintner.isActive(), is(true));
        assertThat(newVintner.isActive(), is(false));
        assertThat(vintnerManager.findByName("newSavedVintner").isPresent(), is(true));
    }

}
