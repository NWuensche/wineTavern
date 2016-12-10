package winetavern.model.user;

import lombok.NonNull;
import org.salespointframework.useraccount.Role;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Enum of all Roles that a {@link Employee} can have. Used for controlling visible sites on the website.
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

    public static Roles of(Role role) {
        return of(role.getName());
    }

    public static Roles of(String roleName) {
        String prefixRole = roleName.startsWith("ROLE_") ? roleName : ROLE_PREFIX + roleName;

        Stream<Roles> allRoles = Arrays.stream(values());
        @NonNull Roles rightRole = allRoles.filter(roles ->
                roles.getNameOfRoleWithPrefix().equals(prefixRole)).findFirst().get();

        return rightRole;
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

}
