package winetavern.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.user.Roles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */
public class ShiftControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Test
    public void showShiftsRight() throws Exception {
        RequestBuilder serviceRequest = post("/admin/management/shifts")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(serviceRequest)
                .andExpect(model().attributeExists("weekStart"))
                .andExpect(view().name("shifts"));
    }

}
