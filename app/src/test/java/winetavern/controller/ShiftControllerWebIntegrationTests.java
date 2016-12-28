package winetavern.controller;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.PersonTitle;
import winetavern.model.user.Roles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */
public class ShiftControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private ShiftRepository shiftRepository;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private UserAccountManager userAccountManager;

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

        TimeInterval now = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        shift = new Shift(now, employee);
        shiftRepository.save(shift);
    }

    @Test
    public void showShiftsRight() throws Exception {
        RequestBuilder request = post("/admin/management/shifts/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("shifts"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void addShiftsGetRight() throws Exception {
        RequestBuilder request = get("/admin/management/shifts/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("employees"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void addShiftsPostRight() throws Exception {
        RequestBuilder request = post("/admin/management/shifts/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("employee", employee.getId().toString())
                .param("date", "11.11.2016")
                .param("start", "08:00")
                .param("end", "11:11");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void removeShiftRight() throws Exception {
        RequestBuilder request = post("/admin/management/shifts/remove/" + shift.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(shiftRepository.findOne(shift.getId()), is(Optional.empty()));
    }

    @Test
    public void changeShiftGetRight() throws Exception {
        RequestBuilder request = get("/admin/management/shifts/change/" + shift.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("time"))
                .andExpect(view().name("shifts"));
    }

    @Test
    public void changeShiftPostRight() throws Exception {
        RequestBuilder request = post("/admin/management/shifts/change/" + shift.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("employee", employee.getId().toString())
                .param("date", "11.11.2016")
                .param("start", "08:00")
                .param("end", "11:11");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(shift.getInterval().getStart(), is(LocalDateTime.of(2016, 11, 11, 8, 0)));
    }

}