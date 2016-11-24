package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.core.Streamable;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Repository interface to handle {@link Person}s
 * @author Niklas Wünsche
 */


public interface PersonManager extends SalespointRepository<Person, Long> {
    Optional<Person> findByUserAccount(UserAccount account);

    @Query(value = "select * from person join user_account ON person.user_account_useraccount_id = user_account.useraccount_id where enabled=1 ", nativeQuery = true)
    ArrayList<Person> findEnabled();
}
