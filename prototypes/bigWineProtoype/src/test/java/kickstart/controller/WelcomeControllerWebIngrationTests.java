package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;


import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.ModelResultMatchers;

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
                .andExpect(status().is3xxRedirection());
        ModelResultMatchers m = model();
        if(m.equals(null)){}
                //.andExpect(model().attributeDoesNotExist("accountcredentials"))
                //.andExpect(model().attributeDoesNotExist("staffCollection"))
                //.andExpect(view().name("users"));
    }

}
