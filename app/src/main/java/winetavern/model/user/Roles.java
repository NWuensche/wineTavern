package winetavern.model.user;

import org.salespointframework.useraccount.Role;

/**
 * @author Niklas Wünsche
 */

public enum Roles {

    ADMIN ("Administrator"),
    SERVICE ("Bedienung"),
    ACCOUNTANT ("Buchhalter"),
    COOK ("Koch");

    private static final String ROLE_PREFIX = "ROLE_";

    private final String germanRoleName;

    Roles(String germanRoleName) {
        this.germanRoleName = germanRoleName;
    }

    public String getNameOfRoleWithPrefix() {
        return ROLE_PREFIX + toString();
    }

    public String getRealNameOfRole() {
        return toString();
    }

    public Role getRole() {
        return Role.of(getNameOfRoleWithPrefix());
    }

    public String getDisplayName() {
        return germanRoleName;
    }

    public static String getGermanNameOfRole(Role role) {
        return getGermanNameOfRole(role.getName());
    }

    public static String getGermanNameOfRole(String role) throws IllegalArgumentException {
        String prefixRole = role.startsWith("ROLE_") ? role : ROLE_PREFIX + role;
        for(Roles r : values()) {
            if(r.getNameOfRoleWithPrefix().equals(prefixRole)) {
                return r.germanRoleName;
            }
        }

        throw new IllegalArgumentException("Role isn't defined: " + role);
    }

}
