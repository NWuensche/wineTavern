package winetavern.model.user;

import org.salespointframework.useraccount.Role;

/**
 * @author Niklas Wünsche
 */

public enum Roles {

    ADMIN ("ROLE_ADMIN"),
    SERVICE ("ROLE_SERVICE"),
    ACCOUNTANT ("ROLE_ACCOUNTANT"),
    COOK ("ROLE_COOK");

    private final String nameOfRole;
    private final Role role;

    Roles(String nameOfRole) {
        this.nameOfRole = nameOfRole;
        this.role = Role.of(nameOfRole);
    }

    public String getNameOfRole() {
        return nameOfRole;
    }

    public Role getRole() {
        return role;
    }

}
