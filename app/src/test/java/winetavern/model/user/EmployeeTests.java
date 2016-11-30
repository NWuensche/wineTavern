package winetavern.model.user;

import static java.lang.reflect.Modifier.PROTECTED;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

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

    @Test(expected = NullPointerException.class)
    public void throwWhenNoAddress() {
        employee = new Employee(acc, null, birthday, personTitle);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenNoBirthday() {
        employee = new Employee(acc, address, null, personTitle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmployeeHas2Roles() {
        acc = userAccountManager.create("testAccount2", "1234",
                Roles.SERVICE.getRole(), Roles.ACCOUNTANT.getRole());
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmployeeHas0Roles() {
        acc = userAccountManager.create("testAccount2", "1234");
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test
    public void isNotEnabled() {
        employee = new Employee(acc, address, birthday, personTitle);
        employeeManager.save(employee);
        acc.setEnabled(false);
        assertThat(employee.isEnabled(), is(false));
    }

    @Test
    public void isEnabled() {
        employee = new Employee(acc, address, birthday, personTitle);
        employeeManager.save(employee);
        acc.setEnabled(true);
        assertThat(employee.isEnabled(), is(true));
    }

    @Test
    public void setAddressRight() {
        employee = new Employee(acc, address, birthday, personTitle);
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

        Employee employee3 = employeeManager
                .findByUserAccount(userAccountManager.findByUsername("testAccount3").get()).get();
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

    @Test
    public void depractedAndProtectedConstructorExists() {
        Constructor<?> protectedConstructor = findProtectedConstructor();
        Annotation annotation = protectedConstructor.getDeclaredAnnotations()[0];
        assertThat(annotation.annotationType().toString(), is(Deprecated.class.toString()));
    }

    /**
     * @implNote Because the place of the constructors in the array can vary, you have search the protected one
     */
    private Constructor<?> findProtectedConstructor() {
        for(Constructor<?> constructor : Employee.class.getDeclaredConstructors()) {
            if(constructor.getModifiers() == PROTECTED) {
                return constructor;
            }
        }
        throw new NoSuchElementException("Protected constructor not found!");
    }

    @Test(expected = DateTimeParseException.class)
    public void throwWhenWrongBirthdayFormat() {
        employee = new Employee(acc, address, "23/12/1996", personTitle);
    }

}
