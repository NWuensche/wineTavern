package winetavern.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;

import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;

/**
 * Web integration tests for the {@link WelcomeController}
 * @author Niklas Wünsche
 */
public class WelcomeControllerWebIngrationTests extends AbstractWebIntegrationTests {

    @Test
    public void redirectsIfAdmin() throws Exception {
        mvc.perform(buildGetAdminRequest("/"))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("backend-temp"));
    }

    @Test
    public void redirectsIfNoAdmin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void redirectToLogin() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

}
