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
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
        userAccountManager.save(account);
        employee = new Employee(account, "Street", "2016/11/11", "Herr");
        employeeManager.save(employee);

        artist = new External("Artist", Money.of(100, EURO));
        externalManager.save(artist);

        groupArtists = new ExpenseGroup("Künstler");
        groupOrder = expenseGroupRepository.findByName("Bestellung").get();
        expenseGroupRepository.save(groupArtists);
        expenseGroupRepository.save(groupOrder);
    }

    @Test
    public void showExpensesRight() throws Exception {
        artist1 = new Expense(Money.of(3, EURO), "Artist", artist, groupArtists);
        artist2 = new Expense(Money.of(100, EURO), "Artist 2", artist, groupArtists);
        employee1 = new Expense(Money.of(100, EURO), "Employee 1", employee, groupOrder);
        employee2 = new Expense(Money.of(200, EURO), "Employee 2", employee, groupOrder);

        expenseRepository.add(artist1);
        expenseRepository.add(artist2);
        expenseRepository.add(employee1);
        expenseRepository.add(employee2);

        RequestBuilder request = post("/accountancy/expenses")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("type", "")
                .param("person", "")
                .param("date", "");

        mvc.perform(request)
                .andExpect(model().attributeExists("expOpenAmount"))
                .andExpect(view().name("expenses"));
    }

    @Test
    // TODO NICHT PUSHEN!
    public void showExpensesRightWithExpenseGroupAndEmployee() throws Exception {
        artist1 = new Expense(Money.of(3, EURO), "Artist", artist, groupArtists);
        artist2 = new Expense(Money.of(100, EURO), "Artist 2", artist, groupArtists);
        employee1 = new Expense(Money.of(100, EURO), "Employee 1", employee, groupOrder);
        employee2 = new Expense(Money.of(200, EURO), "Employee 2", employee, groupOrder);

        expenseRepository.add(artist1);
        expenseRepository.add(artist2);
        expenseRepository.add(employee1);
        expenseRepository.add(employee2);

        RequestBuilder request = post("/accountancy/expenses")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("type", "" + groupOrder.getId())
                .param("person", employee.getId().toString())
                .param("date", "today");

        mvc.perform(request)
                .andExpect(model().attribute("expOpen", Sets.newSet(employee1, employee2))) // TODO geht manchmal nicht.
                .andExpect(view().name("expenses"));
    }

    @Test
    public void showExpensesRightWithExpenseGroupAndEmployeeCloseExpense() throws Exception {
        artist1 = new Expense(Money.of(3, EURO), "Artist", artist, groupArtists);
        artist2 = new Expense(Money.of(100, EURO), "Artist 2", artist, groupArtists);
        employee1 = new Expense(Money.of(100, EURO), "Employee 1", employee, groupOrder);
        employee2 = new Expense(Money.of(200, EURO), "Employee 2", employee, groupOrder);

        expenseRepository.add(artist1);
        expenseRepository.add(artist2);
        expenseRepository.add(employee1);
        expenseRepository.add(employee2);

        RequestBuilder request = post("/accountancy/expenses")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("type", "" + groupOrder.getId())
                .param("person", employee.getId().toString())
                .param("cover", employee1.getId() + "|");

        mvc.perform(request)
                .andExpect(model().attribute("expOpen", Sets.newSet(employee2)))
                .andExpect(model().attribute("expCovered", Sets.newSet(employee1)))
                .andExpect(view().name("expenses"));

        boolean[] newAbrechnungExists = {false};

        List<AccountancyEntry> test = Helper.convertToList(expenseRepository.findAll());

        expenseRepository.findAll().forEach(exp -> {
            if(exp.getDescription().contains("Abrechnung") && exp.getDescription().contains("Hans-Peter Maffay")) {
                newAbrechnungExists[0] = true;
            }
        });

        assertThat(newAbrechnungExists[0], is(true));
    }

    @Test
    public void payoffForEmployeesRight() throws Exception {
        artist1 = new Expense(Money.of(3, EURO), "Artist", artist, groupArtists);
        artist2 = new Expense(Money.of(100, EURO), "Artist 2", artist, groupArtists);
        employee1 = new Expense(Money.of(100, EURO), "Employee 1", employee, groupOrder);
        employee1.cover();
        employee2 = new Expense(Money.of(200, EURO), "Employee 2", employee, groupOrder);

        expenseRepository.add(artist1);
        expenseRepository.add(artist2);
        expenseRepository.add(employee1);
        expenseRepository.add(employee2);

        RequestBuilder request = post("/accountancy/expenses/payoff/" + employee.getId() + "/pay")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(employee2.isCovered(), is(true));

        boolean[] newAbrechnungExists = {false};

        List<AccountancyEntry> test = Helper.convertToList(expenseRepository.findAll());

        expenseRepository.findAll().forEach(exp -> {
            if(exp.getDescription().contains("Tagesabrechnung") && exp.getDescription().contains("Hans-Peter Maffay") && exp.getValue().equals(Money.of(200, EURO))) {
                newAbrechnungExists[0] = true;
            }
        });

        assertThat(newAbrechnungExists[0], is(true));

    }

}
