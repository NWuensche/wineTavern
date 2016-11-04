package winetavern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import winetavern.controller.LoginRequests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Tests for Data initialization and login behavior
 * @author Niklas Wünsche
 */

public class ApplicationTests extends AbstractWebIntegrationTests {

    @Autowired UserAccountManager userAccountManager;
    @Autowired Application application;

    @Test
    public void redirectsAfterLogin() throws Exception {
        RequestBuilder adminLoginRequest = LoginRequests.getLoginRequest("admin", "1234");
        mvc.perform(adminLoginRequest)
                .andExpect(status().is3xxRedirection());
                // TODO NoModelAndView Exception?
                //.andExpect(model().attributeExists("accountcredentials"))
                //.andExpect(view().name("welcome"));
    }


    @Test
    public void errorAfterWrongLogin() throws Exception{
        RequestBuilder wrongLoginRequest = LoginRequests.getLoginRequest("admin", "wrong pass");
        mvc.perform(wrongLoginRequest)
                .andExpect(status().is3xxRedirection());
                // TODO NoModelAndView Exception?
                //.andExpect(model().attributeDoesNotExist("accountcredentials"))
                //.andExpect(view().name("login"));
    }

}
