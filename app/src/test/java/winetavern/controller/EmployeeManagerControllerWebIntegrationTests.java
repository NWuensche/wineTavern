package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Test class für {@link EmployeeManagerController}
 * @author Niklas Wünsche
 */

@Transactional
public class EmployeeManagerControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired
    EmployeeManager employeeManager;
    @Autowired UserAccountManager userAccountManager;

    private String userName;
    private String password;

    @Before
    public void before() {
        userName = "userName";
        password = "1234";
    }

    @Test
    public void redirectToUsers() throws Exception {
        mvc.perform(get("/admin/management/users").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole())))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }

    private RequestBuilder createRequestBuilder(String userName, String password) {
        RequestBuilder request = post("/admin/management/users/add").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("personTitle", "Herr")
                .param("firstName", "Hans")
                .param("lastName", "Müller")
                .param("birthday", "1990/12/12")
                .param("username", userName)
                .param("password", password)
                .param("role", Roles.COOK.getNameOfRoleWithPrefix())
                .param("address", "Mein Haus");
        return request;
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoName() throws Exception {
        RequestBuilder request = createRequestBuilder(null, password);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoPassword() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, null);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenTwoUsersWithSameName() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
        mvc.perform(request);
    }

    @Test
    public void savedNewEmployee() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, password);
        mvc.perform(request);

        Optional<UserAccount> user = userAccountManager.findByUsername(userName);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getRoles().stream().findFirst().get(), is(Role.of("ROLE_COOK")));
        assertThat(user.get().getUsername(), is("userName"));
        assertThat(user.get().getFirstname(), is("Hans"));
        assertThat(user.get().getLastname(), is("Müller"));

        Optional<Employee> employee = employeeManager.findByUserAccount(user.get());
        assertThat(employee.isPresent(), is(true));
        assertThat(employee.get().getBirthday().get(), is(LocalDate.of(1990,12,12)));
        assertThat(employee.get().getAddress().get(), is("Mein Haus"));
        assertThat(employee.get().getDisplayNameOfRole(), is("Koch"));
        assertThat(employee.get().getPersonTitle(), is("Herr"));
    }

    @Test
    public void changeNewEmployee() throws Exception{
        saveNewEmployee();
        String employeeId = getEmployeeId();

        mvc.perform(get("/admin/management/users/edit/" + employeeId).with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("firstName", "DontSave")
                .param("lastName", "Schwab")
                .param("address", "Best House")
                .param("role", Roles.SERVICE.getNameOfRoleWithPrefix()));

        Employee changedEmployee = employeeManager.findByUserAccount(userAccountManager.findByUsername(userName).get()).get();

        assertThat(changedEmployee.getUserAccount().getFirstname(), is("Hans"));
        assertThat(changedEmployee.getUserAccount().getLastname(), is("Schwab"));
        assertThat(changedEmployee.getAddress().get(), is("Best House"));
        assertThat(changedEmployee.getUserAccount().getRoles().stream().findFirst().get(), is(Roles.SERVICE.getRole()));

    }

    private void saveNewEmployee() throws Exception{
        RequestBuilder request = createRequestBuilder(userName, password);
        mvc.perform(request);
    }

    private String getEmployeeId() {
        return employeeManager.findByUserAccount(userAccountManager.findByUsername(userName).get()).get().getId().toString();
    }

    @Test
    public void deleteEmployee() throws Exception{
        saveNewEmployee();
        String employeeId = getEmployeeId();

        mvc.perform(get("/admin/management/users/disable/" + employeeId).with(user("admin").roles(Roles.ADMIN.getRealNameOfRole())));

        Employee deletedEmployee = employeeManager.findByUserAccount(userAccountManager.findByUsername(userName).get()).get();

        assertThat(deletedEmployee.isEnabled(), is(false));
        assertThat(deletedEmployee.getUserAccount().isEnabled(), is(false));

    }

}
