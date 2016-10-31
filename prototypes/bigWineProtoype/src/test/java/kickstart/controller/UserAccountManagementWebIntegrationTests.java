package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test class für UserAccountManagment
 * @author Niklas Wünsche
 */

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
        mvc.perform(request);
    }

}
