package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web integration tests for the {@link WelcomeController}
 * @author Niklas Wünsche
 */

public class WelcomeControllerWebIngrationTests extends AbstractWebIntegrationTests {

    @Autowired WelcomeController welcomeController;
    @Autowired UserAccountManager userAccountManager;

    @Test
    public void redirectToLogin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("accountcredentials"))
                .andExpect(view().name("login"));
    }

    @Test
    public void redirectToUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("accountcredentials"))
                .andExpect(model().attributeExists("staffCollection"))
                .andExpect(view().name("users"));
    }

}
