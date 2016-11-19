package winetavern.model.accountancy;

import org.salespointframework.core.SalespointRepository;

import java.util.Optional;

/**
 * @author Louis
 */

public interface ExpenseGroupRepository extends SalespointRepository<ExpenseGroup, Long> {
    Optional<ExpenseGroup> findByName(String name);
}
