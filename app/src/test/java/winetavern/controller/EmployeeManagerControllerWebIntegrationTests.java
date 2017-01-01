package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static winetavern.RequestHelper.buildGetAdminRequest;
import static winetavern.RequestHelper.buildPostAdminRequest;

import org.junit.Before;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.RequestHelper;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

/**
 * Test class für {@link EmployeeManagerController}
 * @author Niklas Wünsche
 */
public class EmployeeManagerControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired EmployeeManager employeeManager;
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
        mvc.perform(buildGetAdminRequest("/admin/management/users"))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }

    private RequestBuilder createAddUserRequest(String userName, String password) {
        return buildPostAdminRequest("/admin/management/users/add")
                .param("personTitle", "Herr")
                .param("firstName", "Hans")
                .param("lastName", "Müller")
                .param("birthday", "1990/12/12")
                .param("username", userName)
                .param("password", password)
                .param("role", Roles.COOK.getNameOfRoleWithPrefix())
                .param("address", "Mein Haus");

    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoName() throws Exception {
        RequestBuilder request = createAddUserRequest(null, password);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoPassword() throws Exception {
        RequestBuilder request = createAddUserRequest(userName, null);

        mvc.perform(request);
    }

    @Test
    public void savedNewEmployee() throws Exception {
        RequestBuilder request = createAddUserRequest(userName, password);
        mvc.perform(request);

        Optional<UserAccount> user = userAccountManager.findByUsername(userName);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getRoles().stream().findFirst().get(), is(Role.of("ROLE_COOK")));
        assertThat(user.map(UserAccount::getUsername), is(Optional.of("userName")));
        assertThat(user.map(UserAccount::getFirstname), is(Optional.of("Hans")));
        assertThat(user.map(UserAccount::getLastname), is(Optional.of("Müller")));

        Optional<Employee> employee = employeeManager.findByUserAccount(user.get());
        assertThat(employee.isPresent(), is(true));
        assertThat(employee.map(Employee::getBirthday), is(Optional.of(LocalDate.of(1990,12,12))));
        assertThat(employee.map(Employee::getAddress), is(Optional.of("Mein Haus")));
        assertThat(employee.map(Employee::getDisplayNameOfRole), is(Optional.of("Koch")));
        assertThat(employee.map(Employee::getPersonTitle), is(Optional.of("Herr")));
    }

    @Test
    public void changeNewEmployee() throws Exception {
        saveNewEmployee();

        RequestBuilder request = buildGetAdminRequest("/admin/management/users/edit/" + getEmployeeId())
                .param("firstName", "DontSave")
                .param("lastName", "Schwab")
                .param("address", "Best House")
                .param("role", Roles.SERVICE.getNameOfRoleWithPrefix());

        mvc.perform(request);


        Employee changedEmployee = employeeManager.findByUsername(userName).get();

        assertThat(changedEmployee.getUserAccount().getFirstname(), is("Hans"));
        assertThat(changedEmployee.getUserAccount().getLastname(), is("Schwab"));
        assertThat(changedEmployee.getAddress(), is("Best House"));
        assertThat(changedEmployee.getUserAccount().getRoles().stream().findFirst().get(), is(Roles.SERVICE.getRole()));
    }

    private void saveNewEmployee() throws Exception{
        RequestBuilder request = createAddUserRequest(userName, password);
        mvc.perform(request);
    }

    private String getEmployeeId() {
        return employeeManager.findByUsername(userName).map(Employee::getId).get().toString();
    }

    @Test
    public void deleteEmployee() throws Exception{
        saveNewEmployee();
        String employeeId = getEmployeeId();

        mvc.perform(buildGetAdminRequest("/admin/management/users/disable/" + employeeId));

        Employee deletedEmployee = employeeManager.findByUsername(userName).get();

        assertThat(deletedEmployee.isEnabled(), is(false));
        assertThat(deletedEmployee.getUserAccount().isEnabled(), is(false));
    }

    @Test
    public void redirectIfUsernamesIsTaken() throws Exception {
        mvc.perform(createAddUserRequest("admin", "1234"))
                .andExpect(model().attribute("usernameTaken", true))
                .andExpect(model().attributeExists("accountcredentials"))
                .andExpect(view().name("users"));
    }

}
