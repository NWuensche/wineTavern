package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import kickstart.AbstractWebIntegrationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Web integration tests for the {@link ErrorController}
 */

public class ErrorControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired ErrorController controller;

    @Test
    public void redirectToWelcome() throws Exception {
        mvc.perform(get("/err0r"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
    }

}
