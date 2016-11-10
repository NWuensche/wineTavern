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

    public String getNameOfRoleWithPrefix() {
        return nameOfRole;
    }

    public String getRealNameOfRole() {
        return nameOfRole.substring(5);
    }

    /**
     * @throws  IllegalArgumentException
     * @return not null
     */
    public static String getDisplayNameRole(Role role) throws IllegalArgumentException{
        if(!role.getName().startsWith("ROLE_")) {
            throw new IllegalArgumentException("Role name has to start with ROLE_ !");
        }

        switch(role.getName()) {
            case "ROLE_ADMIN":
                return "Administrator";
            case "ROLE_SERVICE":
                return "Bedienung";
            case "ROLE_ACCOUNTANT":
                return "Buchhalter";
            case "ROLE_COOK":
                return "Koch";
            default:
                throw new IllegalArgumentException();
        }
    }

    public Role getRole() {
        return role;
    }

}
