package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.accountancy.BillItem;
import winetavern.model.accountancy.BillItemRepository;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.PersonTitle;
import winetavern.model.user.Roles;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class DashboardControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private BillItemRepository billItemRepository;

    @Before
    public void before() {
        UserAccount service = userAccountManager.create("service", "1234", Roles.SERVICE.getRole());
        UserAccount cook = userAccountManager.create("cook", "1234", Roles.COOK.getRole());
        UserAccount acc = userAccountManager.create("accountant", "1234", Roles.ACCOUNTANT.getRole());
        userAccountManager.save(service);
        userAccountManager.save(cook);
        userAccountManager.save(acc);

        Employee serviceE = new Employee(service, "asd", "2016/12/12", PersonTitle.MISTER.getGerman());
        Employee cookE = new Employee(cook, "asd", "2016/12/12", PersonTitle.MISTER.getGerman());
        Employee accE = new Employee(acc, "asd", "2016/12/12", PersonTitle.MISTER.getGerman());
        employeeManager.save(Arrays.asList(serviceE, cookE, accE));
    }

    @Test
    public void showDashboardAsAdminRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/dashboard"))
                .andExpect(model().attributeExists("shifts"))
                .andExpect(view().name("startadmin"));
    }

    @Test
    public void showDashboardAsServiceRight() throws Exception {
        mvc.perform(post("/dashboard").with(user("service").roles(Roles.SERVICE.getRealNameOfRole())))
                .andExpect(model().attributeExists("time"))
                .andExpect(view().name("startservice"));
    }

    @Test
    public void showDashboardAsCookRight() throws Exception {
        mvc.perform(post("/dashboard").with(user("cook").roles(Roles.COOK.getRealNameOfRole())))
                .andExpect(model().attributeExists("time"))
                .andExpect(view().name("startcook"));
    }

    @Test
    public void showDashboardAsAccountantRight() throws Exception {
        mvc.perform(post("/dashboard").with(user("accountant").roles(Roles.ACCOUNTANT.getRealNameOfRole())))
                .andExpect(model().attributeExists("time"))
                .andExpect(view().name("startcook"));
    }

    @Test
    public void cookReadyRight() throws Exception {
        DayMenuItem dItem = new DayMenuItem("name", "desc", Money.of(3, EURO), 5.5);
        dayMenuItemRepository.save(dItem);
        BillItem bItem = new BillItem(dItem);
        billItemRepository.save(bItem);

        Given:
        assertThat(bItem.getReady(), is(false));

        When:
        mvc.perform(post("/cook/ready/".concat(Long.toString(bItem.getId()))).with(user("accountant").roles(Roles.ACCOUNTANT.getRealNameOfRole())))
                .andExpect(status().is3xxRedirection());

        Then:
        assertThat(bItem.getReady(), is(true));
    }

}