package winetavern.controller;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.RequestHelper;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.PersonTitle;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

/**
 * @author Niklas Wünsche
 */
public class ShiftControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private ShiftRepository shiftRepository;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private UserAccountManager userAccountManager;
    @Autowired private ShiftController shiftController;

    private Employee employee;
    private Shift shift;

    @Before
    public void before() {
        UserAccount userAccount = userAccountManager.create("Test", "Test", Role.of(Roles.ADMIN.getRealNameOfRole()));
        userAccount.setFirstname("Hans");
        userAccount.setLastname("Müller");
        userAccountManager.save(userAccount);
        employee = new Employee(userAccount, "Address", "2016/11/11", PersonTitle.MISTER.getGerman());
        employeeManager.save(employee);

        TimeInterval now = new TimeInterval(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(3));
        shift = new Shift(now, employee);
        shiftRepository.save(shift);
    }

    @Test
    public void showShiftsRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/management/shifts/"))
                .andExpect(model().attributeExists("shifts"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void addShiftsGetRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/admin/management/shifts/add"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void addShiftsPostRight() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/admin/management/shifts/add")
                .param("employee", employee.getId().toString())
                .param("date", "11.11.2016")
                .param("start", "08:00")
                .param("end", "11:11");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void removeShiftRight() throws Exception {
        mvc.perform(buildPostAdminRequest("/admin/management/shifts/remove/" + shift.getId()))
                .andExpect(status().is3xxRedirection());

        assertThat(shiftRepository.findOne(shift.getId()), is(Optional.empty()));
    }

    @Test
    public void changeShiftGetRight() throws Exception {
        mvc.perform(buildGetAdminRequest("/admin/management/shifts/change/" + shift.getId()))
                .andExpect(model().attributeExists("time"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void changeShiftPostRight() throws Exception {
        RequestBuilder request = buildPostAdminRequest("/admin/management/shifts/change/" + shift.getId())
                .param("employee", employee.getId().toString())
                .param("date", "11.11.2016")
                .param("start", "08:00")
                .param("end", "11:11");


        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(shift.getInterval().getStart(), is(LocalDateTime.of(2016, 11, 11, 8, 0)));
    }

    @Test
    public void sortListRight() {
        Shift earlierShift = new Shift(shift.getInterval().returnMovedIntervalByMinutes(-10), employee);
        shiftRepository.save(earlierShift);

        List<Shift> shifts = shiftController.getShiftsOfDay(LocalDate.now());

        assertThat(shifts, is(Arrays.asList(earlierShift, shift)));
    }

}