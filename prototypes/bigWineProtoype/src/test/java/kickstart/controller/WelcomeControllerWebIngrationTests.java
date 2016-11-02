package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
                .andExpect(model().attributeExists("registercredentials"))
                .andExpect(view().name("login"));
    }

    @Test
    public void adminInDB() throws Exception {
        Role adminRole = Role.of("ADMIN");
        Optional<UserAccount> admin;

        mvc.perform(get("/"));
        admin = userAccountManager.findByUsername("admin");

        assertThat(admin.isPresent(), is(true));
        assertThat(admin.get().hasRole(adminRole), is(true));
    }

}
