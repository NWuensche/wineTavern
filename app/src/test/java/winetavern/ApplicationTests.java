package winetavern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;

/**
 * Tests for Data initialization and login behavior
 * @author Niklas Wünsche
 */

public class ApplicationTests extends AbstractWebIntegrationTests {

    @Test
    public void redirectIfNotLoggedIn() throws Exception{
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

}
