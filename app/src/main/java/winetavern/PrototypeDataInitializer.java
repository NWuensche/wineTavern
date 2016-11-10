package winetavern;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import winetavern.model.DateParameter;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

/**
 * Initializes Data in the Database for the prototype
 * @author Niklas Wünsche
 */

@Component
public class PrototypeDataInitializer implements DataInitializer{

    private final UserAccountManager manager;
    private final PersonManager personManager;

    @Autowired
    public PrototypeDataInitializer(UserAccountManager manager, PersonManager personManager) {
        Assert.notNull(manager, "UserAccountManager must not be null!");
        Assert.notNull(personManager, "PersonManager must not be null!");

        this.manager = manager;
        this.personManager = personManager;
    }

    @Override
    public void initialize() {
        initializeAdmin(manager);
    }

    private void initializeAdmin(UserAccountManager manager) {
        String adminName = "admin";

            if(!isAdminInDB(manager, adminName)) {
                UserAccount admin = manager.create(adminName, "1234", Role.of("ROLE_ADMIN"));
                admin.setFirstname("Hans-Peter");
                admin.setLastname("Maffay");
                admin.setEmail("peter.maffay@t-online.de");
                manager.save(admin);
                DateParameter date = new DateParameter();
                date.setDay(15);
                date.setMonth(7);
                date.setYear(1979);
                personManager.save(new Person(admin, "Wundstraße 7, 01217 Dresden", date));
            }
        }

    private boolean isAdminInDB(UserAccountManager manager, String adminName) {
        return manager.findByUsername(adminName).isPresent();
    }

}

