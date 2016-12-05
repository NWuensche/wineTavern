package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

/**
 * Created by michel on 13.11.16.
 */
@Transactional
public class DayMenuManagerWebIntegrationTests extends AbstractWebIntegrationTests {
    @Autowired DayMenuManager dayMenuManager;
    @Autowired DayMenuRepository dayMenuRepository;

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

}
