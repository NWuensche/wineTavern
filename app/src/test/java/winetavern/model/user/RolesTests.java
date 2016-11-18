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
        assertThat(Roles.getDisplayNameRole(Role.of("ROLE_ADMIN")), is("Administrator"));
        assertThat(Roles.getDisplayNameRole("ROLE_ADMIN"), is("Administrator"));
    }

    @Test
    public void isServiceDataRight() {
        Roles admin = Roles.SERVICE;
        assertThat(admin.getRole(), is(Role.of("ROLE_SERVICE")));
        assertThat(admin.getNameOfRoleWithPrefix(), is("ROLE_SERVICE"));
        assertThat(admin.getRealNameOfRole(), is("SERVICE"));
        assertThat(Roles.getDisplayNameRole(Role.of("ROLE_SERVICE")), is("Bedienung"));
        assertThat(Roles.getDisplayNameRole("ROLE_SERVICE"), is("Bedienung"));
    }

    @Test
    public void isAccountantDataRight() {
        Roles admin = Roles.ACCOUNTANT;
        assertThat(admin.getRole(), is(Role.of("ROLE_ACCOUNTANT")));
        assertThat(admin.getNameOfRoleWithPrefix(), is("ROLE_ACCOUNTANT"));
        assertThat(admin.getRealNameOfRole(), is("ACCOUNTANT"));
        assertThat(Roles.getDisplayNameRole(Role.of("ROLE_ACCOUNTANT")), is("Buchhalter"));
        assertThat(Roles.getDisplayNameRole("ROLE_ACCOUNTANT"), is("Buchhalter"));

    }

    @Test
    public void isCookDataRight() {
        Roles admin = Roles.COOK;
        assertThat(admin.getRole(), is(Role.of("ROLE_COOK")));
        assertThat(admin.getNameOfRoleWithPrefix(), is("ROLE_COOK"));
        assertThat(admin.getRealNameOfRole(), is("COOK"));
        assertThat(Roles.getDisplayNameRole(Role.of("ROLE_COOK")), is("Koch"));
        assertThat(Roles.getDisplayNameRole("ROLE_COOK"), is("Koch"));
    }

}
