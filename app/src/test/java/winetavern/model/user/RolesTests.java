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
    }

}
