package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.Roles;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */

public class EventControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired EventCatalog eventCatalog;
    private Event event;
    private long numberOfEventsInDataInit;

    @Before
    public void before() {
        numberOfEventsInDataInit = 2;
        TimeInterval timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        event = new Event("Event", Money.of(3, EURO), timeInterval, "Descritpion");
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
                .param("name", event.getName())
                .param("price", event.getPrice().toString())
                .param("categories", event.getCategories().toString())
                .param("metric", Metric.UNIT.toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(numberOfEventsInDataInit + 1));

        Event storedEvent = Helper.getFirstItem(eventCatalog.findByName(event.getName()));

        assertThat(storedEvent.compareTo(event), is(0));
    }

    @Test
    public void removeEventRight() throws Exception {
        eventCatalog.save(event);

        RequestBuilder request = post("/admin/events/remove")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("event", event.getId().toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(eventCatalog.count(), is(numberOfEventsInDataInit + 0));
    }

}
