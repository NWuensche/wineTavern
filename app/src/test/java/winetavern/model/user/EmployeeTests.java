package winetavern.model.user;

import static java.lang.reflect.Modifier.PROTECTED;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Test class for {@link Employee}
 * @author Niklas Wünsche
 */
public class EmployeeTests {

    private Employee employee;
    private String address;
    private UserAccount acc;
    private String birthday;
    private String personTitle = PersonTitle.MISTER.getGerman();

    @Before
    public void setUp() {
        acc = new UserAccount();
        acc.setFirstname("Hans");
        acc.setLastname("Müller");
        acc.add(Roles.SERVICE.getRole());
        acc.setEnabled(true);
        address = "Neuer Weg 3, 04912 Berlin";
        birthday = "1980/12/30";
    }

    @Test
    public void displayedRoleOfEmployeeIsRight() {
        employee = new Employee(acc, address, birthday, personTitle);
        assertThat(employee.getDisplayNameOfRole(), is("Bedienung"));
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
        acc = new UserAccount();
        acc.add(Roles.ADMIN.getRole());
        acc.add(Roles.SERVICE.getRole());
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenEmployeeHas0Roles() {
        acc = new UserAccount();
        employee = new Employee(acc, address, birthday, personTitle);
    }

    @Test
    public void enableRight() {
        employee = new Employee(acc, address, birthday, personTitle);
        assertThat(employee.isEnabled(), is(true));

        acc.setEnabled(false);
        assertThat(employee.isEnabled(), is(false));
    }

    @Test
    public void setAddressRight() {
        employee = new Employee(acc, address, birthday, personTitle);
        String newAddress = "New Address";
        employee.setAddress(newAddress);
        assertThat(employee.getAddress(), is(newAddress));
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
