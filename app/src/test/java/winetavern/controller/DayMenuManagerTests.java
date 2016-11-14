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

import java.util.Calendar;

/**
 * Created by michel on 13.11.16.
 */
@Transactional
public class DayMenuManagerTests extends AbstractWebIntegrationTests {
    @Autowired DayMenuManager dayMenuManager;
    @Autowired DayMenuRepository dayMenuRepository;

    @Test
    public void createDayMenuWithWrongYear() throws Exception {
        RequestBuilder request = post("/admin/addMenu").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("day", "30")
                .param("month", "10")
                .param("year", "as2013");
        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void createDayMenuWithRealDate() throws Exception {
        RequestBuilder request = post("/admin/addMenu").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("day", "09")
                .param("month", "11")
                .param("year", "1918");
        mvc.perform(request);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.YEAR, 1918);

        DayMenu dayMenu = dayMenuRepository.findByDay(calendar);
        assertNull(dayMenu);
    }

}
