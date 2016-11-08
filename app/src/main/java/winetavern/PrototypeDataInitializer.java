package winetavern;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initializes Data in the Database for the prototype
 * @author Niklas Wünsche
 */

@Component
public class PrototypeDataInitializer implements DataInitializer{

    private final UserAccountManager manager;

    @Autowired
    public PrototypeDataInitializer(UserAccountManager manager) {
        Assert.notNull(manager, "UserAccountManager must not be null!");

        this.manager = manager;
    }

    @Override
    public void initialize() {
        initializeAdmin(manager);
    }

    private void initializeAdmin(UserAccountManager manager) {
        String adminName = "admin";

        if(!isAdminInDB(manager, adminName)) {
                UserAccount admin = manager.create(adminName, "1234", Role.of("ROLE_ADMIN"));
                manager.save(admin);
            }
        }

    private boolean isAdminInDB(UserAccountManager manager, String adminName) {
        return manager.findByUsername(adminName).isPresent();
    }

}

