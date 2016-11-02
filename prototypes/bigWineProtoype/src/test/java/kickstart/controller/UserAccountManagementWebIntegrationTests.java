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
        String userName = "testAccount";
        String password = null;

        RequestBuilder request = createRequestBuilder(userName, password);

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
        String userName = null;
        String password = "1234";

        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
    }

    @Test (expected = NestedServletException.class)
    public void throwWhenTwoUsersWithSameName() throws Exception {
        String userName = "testAccount";
        String password = "";

        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
        mvc.perform(request);
    }

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
