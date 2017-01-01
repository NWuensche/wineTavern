package winetavern.controller;

import org.junit.Test;
import winetavern.AbstractWebIntegrationTests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class DashboardControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Test
    public void showDashboardRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/dashboard"))
                .andExpect(model().attributeExists("shifts"))
                .andExpect(view().name("startadmin"));
    }

}