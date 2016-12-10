package winetavern.model.user;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;
import org.salespointframework.useraccount.Role;

import java.util.NoSuchElementException;

/**
 * Tests for {@link Roles}
 * @author Niklas Wünsche
 */
public class RolesTests {

    @Test
    public void isAdminDataRight() {
        Roles admin = Roles.ADMIN;
        assertThat(admin.getRole(), is(Role.of("ROLE_ADMIN")));
        assertThat(admin.getNameOfRoleWithPrefix(), is("ROLE_ADMIN"));
        assertThat(Roles.of(Role.of("ROLE_ADMIN")), is(admin));
    }

    @Test
    public void isServiceDataRight() {
        Roles service = Roles.SERVICE;
        assertThat(service.getRole(), is(Role.of("ROLE_SERVICE")));
        assertThat(service.getNameOfRoleWithPrefix(), is("ROLE_SERVICE"));
        assertThat(Roles.of(Role.of("ROLE_SERVICE")), is(service));
    }

    @Test
    public void isAccountantDataRight() {
        Roles accountant = Roles.ACCOUNTANT;
        assertThat(accountant.getRole(), is(Role.of("ROLE_ACCOUNTANT")));
        assertThat(accountant.getNameOfRoleWithPrefix(), is("ROLE_ACCOUNTANT"));
        assertThat(Roles.of(Role.of("ROLE_ACCOUNTANT")), is(accountant));
    }

    @Test
    public void isCookDataRight() {
        Roles cook = Roles.COOK;
        assertThat(cook.getRole(), is(Role.of("ROLE_COOK")));
        assertThat(cook.getNameOfRoleWithPrefix(), is("ROLE_COOK"));
        assertThat(Roles.of(Role.of("ROLE_COOK")), is(cook));
    }

    @Test(expected = NoSuchElementException.class)
    public void throwsWhenFalseRole() {
        Roles.of("NOROLE");
    }

    @Test
    public void withMissingRolePrefix() {
        assertThat(Roles.of(Role.of("ACCOUNTANT")), is(Roles.ACCOUNTANT));
    }

}
