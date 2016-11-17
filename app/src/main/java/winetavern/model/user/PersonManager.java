package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import java.util.Optional;

/**
 * Repository interface to handle {@link Person}s
 * @author Niklas Wünsche
 */


public interface PersonManager extends SalespointRepository<Person, Long> {
    Optional<Person> findByUserAccount(UserAccount userAccount);
}
