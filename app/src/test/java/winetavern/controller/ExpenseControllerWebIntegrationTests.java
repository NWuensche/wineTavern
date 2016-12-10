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
public class ExpenseControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Test
    public void showExpensesRight() throws Exception{
        RequestBuilder request = post("/accountancy/expenses")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("type", "3")
                .param("person", "")
                .param("date", "");


        mvc.perform(request)
                .andExpect(model().attributeExists("expenseAmount"))
                .andExpect(view().name("expenses"));
    }
}
