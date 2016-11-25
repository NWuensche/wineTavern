package winetavern.model.accountancy;

import org.salespointframework.core.SalespointRepository;

import java.util.List;

/**
 * @author Louis
 */

public interface BillRepository extends SalespointRepository<Bill, Long> {
    List<Bill> findByIsClosedFalse();
    List<Bill> findByIsClosedTrue();
}
