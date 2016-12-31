package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.user.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static winetavern.controller.RequestHelper.buildGetAdminRequest;
import static winetavern.controller.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */

@Transactional
public class ExpenseControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired ExpenseGroupRepository expenseGroupRepository;
    @Autowired Accountancy expenseRepository;
    @Autowired EmployeeManager employeeManager;
    @Autowired UserAccountManager userAccountManager;
    @Autowired ExternalManager externalManager;

    ExpenseGroup groupArtists;
    ExpenseGroup groupOrder;
    Expense artist1;
    Expense artist2;
    Expense employee1;
    Expense employee2;
    Employee employee;
    External artist;

    @Before
    public void before() {
        UserAccount account = userAccountManager.create("Hans", "1234", Roles.ADMIN.getRole());
        account.setLastname("Roland");
        userAccountManager.save(account);
        employee = new Employee(account, "Street", "2016/11/11", "Herr");
        employeeManager.save(employee);

        artist = new External("Artist", Money.of(100, EURO));
        externalManager.save(artist);

        groupArtists = new ExpenseGroup("Künstler");
        groupOrder = expenseGroupRepository.findByName("Bestellung").get();
        expenseGroupRepository.save(groupArtists);
        expenseGroupRepository.save(groupOrder);

        artist1 = new Expense(Money.of(3, EURO), "Artist", artist, groupArtists);
        artist2 = new Expense(Money.of(100, EURO), "Artist 2", artist, groupArtists);
        employee1 = new Expense(Money.of(100, EURO), "Employee 1", employee, groupOrder);
        employee2 = new Expense(Money.of(200, EURO), "Employee 2", employee, groupOrder);

        expenseRepository.add(artist1);
        expenseRepository.add(artist2);
        expenseRepository.add(employee1);
        expenseRepository.add(employee2);
    }

    @Test
    public void showExpensesRight() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "");
        params.put("person", "");
        params.put("date", "");

        mvc.perform(buildPostAdminRequest("/accountancy/expenses", params))
                .andExpect(model().attributeExists("expOpenAmount"))
                .andExpect(view().name("expenses"));
    }

    @Test
    public void showExpensesRightWithExpenseGroupAndEmployee() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "" + groupOrder.getId());
        params.put("person", employee.getId().toString());
        params.put("date", "today");

        mvc.perform(buildPostAdminRequest("/accountancy/expenses", params))
                .andExpect(model().attributeExists("expOpen"))
                .andExpect(view().name("expenses"));
    }

    @Test
    public void showExpensesRightWithExpenseGroupAndEmployeeCloseExpense() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "" + groupOrder.getId());
        params.put("person", employee.getId().toString());
        params.put("cover", employee1.getId() + "|");

        mvc.perform(buildPostAdminRequest("/accountancy/expenses", params))
                .andExpect(model().attributeExists("expCovered"))
                .andExpect(view().name("expenses"));

        boolean accountancyExists = StreamSupport
                .stream(expenseRepository.findAll().spliterator(), false)
                .anyMatch(exp -> exp.getDescription().contains("Abrechnung") && exp.getDescription().contains("Hans-Peter Roch"));

        assertTrue(accountancyExists);
    }

    @Test
    public void showPayoffRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/accountancy/expenses/payoff/"))
                .andExpect(model().attributeExists("service"))
                .andExpect(view().name("payoff"));
    }

    @Test
    public void redirectPayoffRight() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("personId", employee.getId().toString());

        mvc.perform(buildPostAdminRequest("/accountancy/expenses/payoff/", params))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void payoffSumRight() throws Exception {
        employee1.cover();

        mvc.perform(buildPostAdminRequest("/accountancy/expenses/payoff/" + employee.getId()))
                .andExpect(view().name("payoff"))
                .andExpect(model().attributeExists("price"));
    }

    @Test
    public void payoffForEmployeesRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/accountancy/expenses/payoff/" + employee.getId() + "/pay"))
                .andExpect(status().is3xxRedirection());

        boolean billOfDayExists = StreamSupport
                .stream(expenseRepository.findAll().spliterator(), false)
                .anyMatch(exp -> exp.getDescription().contains("Tagesabrechnung") && exp.getDescription().contains("Hans-Peter Roch"));

        assertTrue(billOfDayExists);
    }

}
