package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test class für {@link PersonManagerController}
 * @author Niklas Wünsche
 */

@Transactional // Rolls the database after the tests back, to remove new Accounts
public class PersonManagerControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired PersonManagerController controller;

    @Test(expected = NestedServletException.class)
    public void throwWhenNoPassword() throws Exception {
        String userName = "testAccount";
        String password = null;

        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
    }

    @Test
    public void redirectToUsers() throws Exception {
        mvc.perform(get("/admin/users").with(user("admin").roles("ADMIN")))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }

    private RequestBuilder createRequestBuilder(String name, String password) {
        RequestBuilder request = post("/admin/users/addNew").with(user("admin").roles("ADMIN"))
                .param("username", name)
                .param("password", password);

        return request;
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoName() throws Exception {
        String userName = null;
        String password = "1234";

        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenTwoUsersWithSameName() throws Exception {
        String userName = "testAccount";
        String password = "1234";

        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
        mvc.perform(request);
    }

    @Test
    public void savedNewUser() throws Exception {
        String username = "testAccount";
        String password = "1234";
        UserAccount user;

        RequestBuilder request = createRequestBuilder(username, password);
        mvc.perform(request);

        user = controller.getUserAccountManager().findByUsername(username).get();
        assertThat(user.getUsername(), is(username));
    }

}
