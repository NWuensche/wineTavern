package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test class für UserAccountManagment
 * @author Niklas Wünsche
 */

@Transactional // Rolls the database after the tests back, to remove new Accounts
public class UserAccountManagementWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired UserAccountManagementController controller;

    @Test(expected = NestedServletException.class)
    public void throwWhenNoPassword() throws Exception {
        RequestBuilder request = createRequestBuilder("R", null);

        mvc.perform(request);
    }

    private RequestBuilder createRequestBuilder(String name, String password) {
        RequestBuilder request = post("/addNew")
                .param("name", name)
                .param("password", password);

        return request;
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoName() throws Exception {
        RequestBuilder request = createRequestBuilder(null, "1234");

        mvc.perform(request);
    }

    @Test (expected = NestedServletException.class)
    public void throwWhenTwoUsersWithSameName() throws Exception {
        RequestBuilder request = createRequestBuilder("N", "1234");

        mvc.perform(request);
    @Test
    public void savedNewUser() throws Exception {
        String userName = "testAccount";
        String password = "1234";
        UserAccount user;

        RequestBuilder request = createRequestBuilder(userName, password);
        mvc.perform(request);

        user = controller.getUserAccountManager().findByUsername(userName).get();
        assertThat(user.getUsername(), is(userName));
    }

}
