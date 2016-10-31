package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Web integration tests for the {@link WelcomeController}
 */

public class WelcomeControllerWebIngrationTests extends AbstractWebIntegrationTests {

    @Autowired WelcomeController welcomeController;

    @Test
    public void redirectToLogin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

}
