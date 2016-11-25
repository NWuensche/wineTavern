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

    private final String ROLE_PREFIX = "ROLE_";

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
        return nameOfRole.substring(ROLE_PREFIX.length());
    }

    /**
     * @return not null
     */
    public static String getDisplayNameRole(Role role) {
        return getDisplayNameRole(role.getName());
    }

    /**
     * @return not null
     */
    public static String getDisplayNameRole(String roleName) throws IllegalArgumentException {
        String roleWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        switch(roleWithPrefix) {
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
