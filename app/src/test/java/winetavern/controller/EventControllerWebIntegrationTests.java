package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.ExternalManager;
import winetavern.model.user.Person;
import winetavern.model.user.Roles;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
public class EventControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired EventCatalog eventCatalog;
    @Autowired ExternalManager externalManager;
    private Event event;

    @Before
    public void before() {
        eventCatalog.deleteAll();
        Person external = externalManager.getFirst();
        TimeInterval timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        event = new Event("Event", Money.of(3, EURO), timeInterval, "Descritpion", external);
        eventCatalog.save(event);
    }

    @Test
    public void manageEventsRight() throws Exception {
        RequestBuilder request = post("/admin/events")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("eventAmount"))
                .andExpect(view().name("events"));
    }

    @Test
    public void addEventRight() throws Exception {
        RequestBuilder request = post("/admin/events/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("name", "New")
                .param("desc", "Desc")
                .param("date", "11.11.2016 11:11 - 11.11.2016 23:11")
                .param("price", "6.66")
                .param("external", event.getPerson().getId() + "")
                .param("externalName", "")
                .param("externalWage", "");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(2l));
        assertTrue(eventCatalog.stream()
                    .anyMatch(event -> event.getName().equals("New")));
    }

    /*
    @Test
    public void removeEventRight() throws Exception {
        eventCatalog.save(event);
        System.out.println("id: "+event.getId());

        RequestBuilder request = get("/admin/events/remove/" + event.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(numberOfEventsInDataInit + 0));
    }
    */

}
