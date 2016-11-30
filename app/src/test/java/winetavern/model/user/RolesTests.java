package winetavern.model.user;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;
import org.salespointframework.useraccount.Role;

/**
 * Tests for {@link Roles}
 */

public class RolesTests {

    @Test
    public void isAdminDataRight() {
        Roles admin = Roles.ADMIN;
        assertThat(admin.getRole(), is(Role.of("ROLE_ADMIN")));
        assertThat(admin.getNameOfRoleWithPrefix(), is("ROLE_ADMIN"));
        assertThat(admin.getRealNameOfRole(), is("ADMIN"));
        assertThat(Roles.of(Role.of("ROLE_ADMIN")), is(admin));
        assertThat(Roles.getGermanNameOfRole("ROLE_ADMIN"), is("Administrator"));
    }

    @Test
    public void isServiceDataRight() {
        Roles service = Roles.SERVICE;
        assertThat(service.getRole(), is(Role.of("ROLE_SERVICE")));
        assertThat(service.getNameOfRoleWithPrefix(), is("ROLE_SERVICE"));
        assertThat(service.getRealNameOfRole(), is("SERVICE"));
        assertThat(Roles.of(Role.of("ROLE_SERVICE")), is(service));
        assertThat(Roles.getGermanNameOfRole("ROLE_SERVICE"), is("Bedienung"));
    }

    @Test
    public void isAccountantDataRight() {
        Roles accountant = Roles.ACCOUNTANT;
        assertThat(accountant.getRole(), is(Role.of("ROLE_ACCOUNTANT")));
        assertThat(accountant.getNameOfRoleWithPrefix(), is("ROLE_ACCOUNTANT"));
        assertThat(accountant.getRealNameOfRole(), is("ACCOUNTANT"));
        assertThat(Roles.of(Role.of("ROLE_ACCOUNTANT")), is(accountant));
        assertThat(Roles.getGermanNameOfRole("ROLE_ACCOUNTANT"), is("Buchhalter"));

    }

    @Test
    public void isCookDataRight() {
        Roles cook = Roles.COOK;
        assertThat(cook.getRole(), is(Role.of("ROLE_COOK")));
        assertThat(cook.getNameOfRoleWithPrefix(), is("ROLE_COOK"));
        assertThat(cook.getRealNameOfRole(), is("COOK"));
        assertThat(Roles.of(Role.of("ROLE_COOK")), is(cook));
        assertThat(Roles.getGermanNameOfRole("ROLE_COOK"), is("Koch"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenFalseRole() {
        Roles.getGermanNameOfRole("NOROLE");
    }

    @Test
    public void withMissingRolePrefix() {
        assertThat(Roles.of(Role.of("ACCOUNTANT")), is(Roles.ACCOUNTANT));
        assertThat(Roles.getGermanNameOfRole("ACCOUNTANT"), is("Buchhalter"));

    }

}
