package winetavern.model.user;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractIntegrationTests;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Test class for {@link Employee}
 */

@Transactional
public class EmployeeTests extends AbstractIntegrationTests{

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private EmployeeManager employeeManager;

    private Employee employee;
    private String address;
    private UserAccount acc;
    private String birthday;
    private String personTitle = PersonTitle.MISTER.getGerman();

    @Before
    public void setUp() {
        acc = userAccountManager.create("testAccount", "1234", Roles.SERVICE.getRole());
        address = "Neuer Weg 3, 04912 Berlin";
        birthday = "1980/12/30";
    }

    @Test
    public void newEmployeeInDB() {
        employee = new Employee(acc, address, birthday, personTitle);
        employeeManager.save(employee);
        assertThat(employeeManager.findOne(employee.getId()).isPresent(), is(true));
        Employee savedEmployee = employeeManager.findOne(employee.getId()).get();
        assertThat(savedEmployee.getBirthday(), is(LocalDate.of(1980, 12, 30)));
        assertThat(savedEmployee.getPersonTitle(), is(PersonTitle.MISTER.getGerman()));
        assertThat(savedEmployee.getUserAccount(), is(acc));
        assertThat(savedEmployee.getRole(), is(Roles.SERVICE.getRole()));
    }

        @Test
    public void displayedRoleOfEmployeeIsRight() {
        employee = new Employee(acc, address, birthday, personTitle);
        employeeManager.save(employee);
        assertThat(employeeManager.findOne(employee.getId()).get().getDisplayNameOfRole(), is("Bedienung"));
    }

    @Test
    public void newEmployeeInDBWithoutAddress() {
        employee = new Employee(acc, null, birthday, personTitle);
        employeeManager.save(employee);
        assertThat(employeeManager.findOne(employee.getId()).get().getAddress(), is(IsNull.nullValue()));
    }

    @Test
    public void newEmployeeInDBWithoutBirthday() {
        employee = new Employee(acc, address, null, personTitle);
        employeeManager.save(employee);
        assertThat(employeeManager.findOne(employee.getId()).get().getBirthday(), is(IsNull.nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmployeeHas2Roles() {
        acc = userAccountManager.create("testAccount2", "1234", Roles.SERVICE.getRole(), Roles.ACCOUNTANT.getRole());
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmployeeHas0Roles() {
        acc = userAccountManager.create("testAccount2", "1234");
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test
    public void isNotEnabled() {
        employee = new Employee(acc, address, null, personTitle);
        employeeManager.save(employee);
        acc.setEnabled(false);
        assertThat(employee.isEnabled(), is(false));
    }

    @Test
    public void isEnabled() {
        employee = new Employee(acc, address, null, personTitle);
        employeeManager.save(employee);
        acc.setEnabled(true);
        assertThat(employee.isEnabled(), is(true));
    }

    @Test
    public void setAddressRight() {
        employee = new Employee(acc, address, null, personTitle);
        String newAddress = "New Address";
        employee.setAddress(newAddress);
        assertThat(employee.getAddress(), is(newAddress));
    }

    @Test
    public void findEnabled() {
        disableAllEmployeeFromInitalizer();

        saveEmployee();
        disableOneEmployee();

        ArrayList<Employee> enabled = employeeManager.findEnabled();

        Employee employee3 = employeeManager.findByUserAccount(userAccountManager.findByUsername("testAccount3").get()).get();
        assertArrayEquals(enabled.toArray(), new Employee[]{employee, employee3});
    }

    private void disableAllEmployeeFromInitalizer() {
        employeeManager.findAll().forEach(employee -> employee.getUserAccount().setEnabled(false));
    }

    private void saveEmployee() {
        UserAccount acc2 = userAccountManager.create("testAccount2", "1234", Roles.SERVICE.getRole());
        UserAccount acc3 = userAccountManager.create("testAccount3", "1234", Roles.SERVICE.getRole());
        employee = new Employee(acc, address, birthday, personTitle);
        Employee employee2 = new Employee(acc2, address, birthday, personTitle);
        Employee employee3 = new Employee(acc3, address, birthday, personTitle);
        employeeManager.save(employee);
        employeeManager.save(employee2);
        employeeManager.save(employee3);
    }

    private void disableOneEmployee() {
        Employee disableEmployee2 = employeeManager.findByUserAccount(userAccountManager.findByUsername("testAccount2").get()).get();
        disableEmployee2.getUserAccount().setEnabled(false);
    }

}
