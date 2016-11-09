package winetavern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.Roles;

import java.util.Optional;

/**
 * Tests for data initialization
 * @author Niklas Wünsche
 */

public class PrototypeDataInitializerTests extends AbstractWebIntegrationTests{

    @Autowired UserAccountManager userAccountManager;

    @Test
    public void adminInDB() throws Exception {
        Optional<UserAccount> admin;

        mvc.perform(get("/"));
        admin = userAccountManager.findByUsername("admin");

        assertThat(admin.isPresent(), is(true));
        assertThat(admin.get().hasRole(Roles.ADMIN.getRole()), is(true));
    }

}
