package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.RequestHelper;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.user.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

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
        RequestBuilder request = buildPostAdminRequest("/accountancy/expenses")
                .param("type", " ")
                .param("person", "")
                .param("date", "");

        mvc.perform(request)
                .andExpect(model().attributeExists("expOpenAmount"))
                .andExpect(view().name("expenses"));
    }

    @Test
    public void showExpensesRightWithExpenseGroupAndEmployee() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/accountancy/expenses")
                .param("type", "" + groupOrder.getId())
                .param("person", employee.getId().toString())
                .param("date", "today");

        mvc.perform(request)
                .andExpect(model().attributeExists("expOpen"))
                .andExpect(view().name("expenses"));
    }

    @Test
    public void showExpensesRightWithExpenseGroupAndEmployeeCloseExpense() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/accountancy/expenses")
                .param("type", "" + groupOrder.getId())
                .param("person", employee.getId().toString())
                .param("cover", employee1.getId() + "|");

        mvc.perform(request)
                .andExpect(model().attributeExists("expCovered"))
                .andExpect(view().name("expenses"));

        boolean accountancyExists = StreamSupport
                .stream(expenseRepository.findAll().spliterator(), false)
                .anyMatch(exp -> exp.getDescription().contains("Abrechnung")
                        && exp.getDescription().contains("Hans-Peter Roch"));

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
        RequestBuilder request = buildPostAdminRequest("/accountancy/expenses/payoff/")
                .param("personId", employee.getId().toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void payoffSumRightWhenExpenseCovered() throws Exception {
        employee1.cover();

        mvc.perform(buildPostAdminRequest("/accountancy/expenses/payoff/" + employee.getId()))
                .andExpect(view().name("payoff"))
                .andExpect(model().attributeExists("price"));
    }

    @Test
    public void payoffSumRightWhenExpenseUncovered() throws Exception {
        Expense expense =
                new Expense(Money.of(3, EURO), "Desc", employee, expenseGroupRepository.findByName("Bestellung").get());

        expenseRepository.add(expense);

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
