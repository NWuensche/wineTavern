package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface to handle {@link Employee}s
 * @author Niklas Wünsche
 */

public interface EmployeeManager extends SalespointRepository<Employee, Long> {
    Optional<Employee> findByUserAccount(UserAccount account);

    @Query(value = "select * from employee " +
            "join user_account ON employee.user_account_useraccount_id = user_account.useraccount_id " +
            "where useraccount_id=?#{[0]} ", nativeQuery = true)
    Optional<Employee> findByUsername(String Username);

    @Query(value = "select * from employee " +
            "join user_account ON employee.user_account_useraccount_id = user_account.useraccount_id where enabled=1 ",
            nativeQuery = true)
    List<Employee> findEnabled();
}
