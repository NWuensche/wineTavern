package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.springframework.validation.BindException;
import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

/**
 * Created by michel on 13.11.16.
 */
@Transactional
public class DayMenuManagerWebIntegrationTests extends AbstractWebIntegrationTests {
    @Autowired private DayMenuManager dayMenuManager;
    @Autowired private DayMenuRepository dayMenuRepository;

    private DayMenu dayMenu;

    @Before
    public void before() {
        dayMenu = new DayMenu(LocalDate.of(2016,11,11));
        dayMenuRepository.save(dayMenu);
    }

    @Test
    public void showMenusRight() throws Exception {
        RequestBuilder request = post("/admin/menu/show")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("menus"))
                .andExpect(view().name("daymenulist"));
    }

    @Test
    public void addMenuGetRight() throws Exception {
        RequestBuilder request = get("/admin/menu/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("date"))
                .andExpect(view().name("addmenu"));
    }

    @Test
    public void createDayMenuWithWrongYear() throws Exception {
        RequestBuilder request = post("/admin/menu/add").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("day", "30")
                .param("month", "10")
                .param("year", "as2013");
        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void createDayMenuWithRealDate() throws Exception {
        RequestBuilder request = post("/admin/menu/add").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("day", "9")
                .param("month", "11")
                .param("year", "1918");
        mvc.perform(request);

        LocalDate givenDate = LocalDate.of(1918, 11, 9);

        boolean[] isDateInRepo = {false};
        Iterable<DayMenu> allDayMenus = dayMenuRepository.findAll();

        allDayMenus.forEach(dayMenu -> {
            LocalDate date = dayMenu.getDay();
            if(date.equals(givenDate)) {
                isDateInRepo[0] = true;
            }
        });
        assertThat(isDateInRepo[0], is(true));
    }

    @Test
    public void removeDayMenuRight() throws Exception {
        DayMenu willBeDeleted = new DayMenu(LocalDate.now());
        dayMenuRepository.save(willBeDeleted);

        RequestBuilder request = post("/admin/menu/remove")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("daymenuid", willBeDeleted.getId().toString());

        mvc.perform(request)
                .andExpect(view().name("daymenulist"));

        assertThat(dayMenuRepository.findOne(willBeDeleted.getId()), is(Optional.empty()));
    }

    @Test
    public void editRight() throws Exception {
        RequestBuilder request = post("/admin/menu/edit/" + dayMenu.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(view().name("editdaymenu"))
                .andExpect(model().attribute("daymenu", dayMenu))
                .andExpect(model().attributeExists("stock"));
    }

}
