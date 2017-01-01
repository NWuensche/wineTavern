package winetavern.controller;

import org.junit.Test;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.RequestHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.AnyOf.*;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class SettingsControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired BusinessTime businessTime;

    @Test
    public void showSettingsRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/admin/settings"))
                .andExpect(view().name("settings"));
    }

    @Test
    public void setBusinessTimeCorrect() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/admin/settings")
                .param("time", "11.11.2016 11:11");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(Duration.between(businessTime.getTime(), LocalDateTime.of(2016, 11, 11, 11, 11)).getSeconds(),
                (anyOf(is(-1l), is(0l), is(1l))));
    }

}
