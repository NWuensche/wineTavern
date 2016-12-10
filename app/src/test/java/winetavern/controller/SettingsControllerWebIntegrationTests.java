package winetavern.controller;

import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.user.Roles;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.AnyOf.*;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */
public class SettingsControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired BusinessTime businessTime;

    @Test
    public void showSettingsRight() throws Exception{
        RequestBuilder request = get("/admin/settings")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11");

        mvc.perform(request)
                .andExpect(view().name("settings"));
    }

    @Test
    public void setBusinessTimeCorrect() throws Exception{
        RequestBuilder request = post("/admin/settings")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("time", "11.11.2016 11:11");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(Duration.between(businessTime.getTime(), LocalDateTime.of(2016, 11, 11, 11, 11)).getSeconds(), (anyOf(is(-1l), is(0l), is(1l))));
    }

}
