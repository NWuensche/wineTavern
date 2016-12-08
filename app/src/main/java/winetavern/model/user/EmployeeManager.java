package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repository interface to handle {@link Employee}s
 * @author Niklas Wünsche
 */

public interface EmployeeManager extends SalespointRepository<Employee, Long> {
    Optional<Employee> findByUserAccount(UserAccount account);

    @Query(value = "select * from person " +
            "join user_account ON person.user_account_useraccount_id = user_account.useraccount_id where enabled=1 and dtype='employee'",
            nativeQuery = true)
    ArrayList<Employee> findEnabled();
}
