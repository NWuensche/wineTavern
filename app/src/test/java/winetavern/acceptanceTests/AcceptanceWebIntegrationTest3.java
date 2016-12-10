package winetavern.acceptanceTests;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.AccountCredentials;
import winetavern.Helper;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.Roles;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author Niklas Wünsche
 */

public class AcceptanceWebIntegrationTest3 extends AbstractWebIntegrationTests {

    @Autowired UserAccountManager userAccountManager;
    @Autowired EmployeeManager employeeManager;
    private Employee service1;
    private Employee service2;

    @Before
    public void setUpServiceInDB() {
        UserAccount serviceUserAccount1 = userAccountManager.create("Kellner 1", "1234", Roles.SERVICE.getRole());
        UserAccount serviceUserAccount2 = userAccountManager.create("Kellner 2", "2345", Roles.SERVICE.getRole());
        userAccountManager.save(serviceUserAccount1);
        userAccountManager.save(serviceUserAccount2);

        service1 = new Employee(serviceUserAccount1, "Zellescher Weg 41", "1980/12/12", "Misses");
        service2 = new Employee(serviceUserAccount2, "Wundtstraße 1", "1970/11/11", "Mister");
        employeeManager.save(service1);
        employeeManager.save(service2);
    }

    @Test
    public void testEmployees() {
        testIf2ServiceMemberInRepo();

        disable2ServiceMember();
        addNewServiceMember();

        assertThat(employeeManager.findEnabled().size(), is(3)); // Admin + 1 Data Init Serivce Member + 1 New Service
        assertThat(employeeManager.findEnabled().contains(service1), is(false));
        assertThat(employeeManager.findEnabled().contains(service2), is(false));

        assertThat(employeeManager.findByUsername("Neuer Kellner").isPresent(), is(true));
    }

    private void testIf2ServiceMemberInRepo() {
        // TODO Remove Data Init Serivce Member
        assertThat(employeeManager.count(), is(3l + 1l)); // Admin + 2 Service Member + 1 Data Init Serivce Member
        assertThat(employeeManager.findByUsername("Kellner 1").isPresent(), is(true));
        assertThat(employeeManager.findByUsername("Kellner 2").isPresent(), is(true));
    }

    private void disable2ServiceMember() {
        try {
            disableServiceNumber("1");
            disableServiceNumber("2");
        }
        catch (Exception e) {
            System.err.println("Error while disabling service members!");
            e.printStackTrace();
        }
    }

    private void disableServiceNumber(String number) throws Exception {
        // TODO New Method for this!
        Employee toRemove = employeeManager
                .findByUserAccount(userAccountManager.findByUsername("Kellner " + number).get()).get();

        RequestBuilder serviceRequest = post("/admin/management/users/disable/" + toRemove.getId().toString())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(serviceRequest);
    }

    private void addNewServiceMember() {
        AccountCredentials newService = new AccountCredentials();
        newService.setUsername("Neuer Kellner");
        newService.setPassword("3456");
        newService.setRole(Roles.SERVICE.getRole().toString());
        newService.setFirstName("Thmoas");
        newService.setLastName("Müller");
        newService.setAddress("Bergstraße 61");
        newService.setBirthday("1960/10/10");
        newService.setPersonTitle("Mister");

        RequestBuilder serviceRequest = post("/admin/management/users/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("personTitle", newService.getPersonTitle())
                .param("firstName", newService.getFirstName())
                .param("lastName", newService.getLastName())
                .param("birthday", newService.getBirthday())
                .param("username", newService.getUsername())
                .param("password", newService.getPassword())
                .param("role", newService.getRole())
                .param("address", newService.getAddress());

        try {
            mvc.perform(serviceRequest);
        }
        catch(Exception e) {
            System.err.println("Error while creating new Service");
            e.printStackTrace();
        }
    }

}
