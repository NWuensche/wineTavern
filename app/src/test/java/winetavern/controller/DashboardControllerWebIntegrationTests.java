package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.accountancy.*;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.reservation.Desk;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.PersonTitle;
import winetavern.model.user.Roles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.stringContainsInOrder;
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
    @Autowired private Accountancy accountancy;
    @Autowired private ExpenseGroupRepository expenseGroupRepository;
    @Autowired private BusinessTime time;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private EventCatalog eventCatalog;

    private Employee serviceE;

    @Before
    public void before() {
        time.forward(Duration.ofNanos(ChronoUnit.NANOS.between(time.getTime(), LocalDateTime.of(2016,11,11, 11, 11))));

        UserAccount service = userAccountManager.create("service", "1234", Roles.SERVICE.getRole());
        UserAccount cook = userAccountManager.create("cook", "1234", Roles.COOK.getRole());
        UserAccount acc = userAccountManager.create("accountant", "1234", Roles.ACCOUNTANT.getRole());
        userAccountManager.save(service);
        userAccountManager.save(cook);
        userAccountManager.save(acc);

        serviceE = new Employee(service, "asd", "2016/12/12", PersonTitle.MISTER.getGerman());
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
                .andExpect(view().name("startadmin"));
    }

    @Test
    public void showDashboardAsDifferentRoleRight() throws Exception {
        userAccountManager.findByUsername("accountant").get().remove(Roles.ACCOUNTANT.getRole());
        mvc.perform(post("/dashboard").with(user("accountant").roles("DIFFERENT")))
                .andExpect(model().attributeDoesNotExist("time"))
                .andExpect(view().name("backend-temp"));
    }

    @Test
    public void showDashboardWithExpensesAsAdminRight() throws Exception {
        Expense positiveExpense = new Expense(Money.of(3, EURO), "desc", employeeManager.findByUsername("admin").get(),
                expenseGroupRepository.findByName("Künstlergage").get());
        Expense negativeExpense = new Expense(Money.of(-3, EURO), "desc", employeeManager.findByUsername("admin").get(),
                expenseGroupRepository.findByName("Künstlergage").get());
        Expense notInList = new Expense(Money.of(-3, EURO), "desc", employeeManager.findByUsername("admin").get(),
                expenseGroupRepository.findByName("Bestellung").get());
        positiveExpense.cover();
        negativeExpense.cover();

        accountancy.add(positiveExpense);
        accountancy.add(negativeExpense);
        accountancy.add(notInList);

        mvc.perform(buildPostAdminRequest("/dashboard"))
                .andExpect(model().attribute("income", "[[\"Tag\",\"Einnahmen\",\"Ausgaben\",\"Schnitt\"]," +
                        "[\"Freitag\",0.0,0.0,0.0],[\"Samstag\",0.0,0.0,0.0],[\"Sonntag\",0.0,0.0,0.0]," +
                        "[\"Montag\",0.0,0.0,0.0],[\"Dienstag\",0.0,0.0,0.0],[\"Mittwoch\",0.0,0.0,0.0]," +
                        "[\"Donnerstag\",0.0,0.0,0.0],[\"Freitag\",3.0,-3.0,0.0]]"))
                .andExpect(view().name("startadmin"));
    }

    @Test
    public void showDashboardWithReservationsAsAdminRight() throws Exception {
        Desk desk = new Desk("table", 4);

        Reservation yesterday = new Reservation("guest", 3, desk,
                new TimeInterval(time.getTime().minusDays(1), time.getTime().minusDays(1)));
        Reservation today = new Reservation("guest", 3, desk,
                new TimeInterval(time.getTime(), time.getTime()));

        reservationRepository.save(Arrays.asList(yesterday, today));

        mvc.perform(buildPostAdminRequest("/dashboard"))
                .andExpect(model().attribute("reservations", stringContainsInOrder(Arrays.asList(
                        "\"table\":\"table\",\"person\":\"guest\",",
                        "\"start\":\"November 11, 2016 11:11:",
                        "\"end\":\"November 11, 2016 11:11:",
                        "\"|"))))
                .andExpect(view().name("startadmin"));
    }

    @Test
    public void showDashboardWithBillsAsServiceRight() throws Exception {
        DayMenuItem dItem = new DayMenuItem("name", "desc", Money.of(3, EURO), 4.5);
        dayMenuItemRepository.save(dItem);
        BillItem bItem = new BillItem(dItem);
        billItemRepository.save(bItem);

        Bill closed = new Bill("Table 1", serviceE);
        Bill open = new Bill("Table 1", serviceE);

        closed.changeItem(bItem, 4);
        closed.close(time);

        billRepository.save(Arrays.asList(open, closed));

        mvc.perform(post("/dashboard").with(user("service").roles(Roles.SERVICE.getRealNameOfRole())))
                .andExpect(model().attribute("income", "[[\"Tag\",\"Ausgaben\",\"Gewinn\"],[\"Freitag\",0.0,0.0]," +
                        "[\"Samstag\",0.0,0.0],[\"Sonntag\",0.0,0.0],[\"Montag\",0.0,0.0],[\"Dienstag\",0.0,0.0]," +
                        "[\"Mittwoch\",0.0,0.0],[\"Donnerstag\",0.0,0.0],[\"Freitag\",0.0,12.0]]"))
                .andExpect(view().name("startservice"));
    }

    @Test
    public void showDashboardWithExpensesAsServiceRight() throws Exception {
        Expense positiveExpense = new Expense(Money.of(3, EURO), "desc", employeeManager.findByUsername("service").get(),
                expenseGroupRepository.findByName("Abrechnung").get());
        Expense negativeExpense = new Expense(Money.of(-4, EURO), "desc", employeeManager.findByUsername("service").get(),
                expenseGroupRepository.findByName("Abrechnung").get());
        Expense notInList = new Expense(Money.of(-3, EURO), "desc", employeeManager.findByUsername("service").get(),
                expenseGroupRepository.findByName("Bestellung").get());

        accountancy.add(positiveExpense);
        accountancy.add(negativeExpense);
        accountancy.add(notInList);

        mvc.perform(post("/dashboard").with(user("service").roles(Roles.SERVICE.getRealNameOfRole())))
                .andExpect(model().attribute("income", "[[\"Tag\",\"Ausgaben\",\"Gewinn\"],[\"Freitag\",0.0,0.0]," +
                        "[\"Samstag\",0.0,0.0],[\"Sonntag\",0.0,0.0],[\"Montag\",0.0,0.0],[\"Dienstag\",0.0,0.0]," +
                        "[\"Mittwoch\",0.0,0.0],[\"Donnerstag\",0.0,0.0],[\"Freitag\",1.0,-1.0]]"))
                .andExpect(view().name("startservice"));
    }

    @Test
    public void showDashboardWithNewsAsServiceRight() throws Exception {
        Event yesterday = new Event("yesterday", Money.of(3, EURO),
                        new TimeInterval(time.getTime().minusDays(1), time.getTime().minusDays(1)), "Desc", serviceE);

        Event today = new Event("awesome today", Money.of(3, EURO),
                new TimeInterval(time.getTime(), time.getTime()), "Desc", serviceE);

        eventCatalog.save(Arrays.asList(yesterday, today));

        mvc.perform(post("/dashboard").with(user("service").roles(Roles.SERVICE.getRealNameOfRole())))
                .andExpect(model().attributeExists("news"))
                .andExpect(view().name("startservice"));
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