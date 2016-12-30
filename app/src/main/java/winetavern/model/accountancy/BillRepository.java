package winetavern.model.accountancy;

import org.salespointframework.core.SalespointRepository;
import winetavern.ExtraRepository;

import java.util.List;

/**
 * @author Louis
 */

public interface BillRepository extends ExtraRepository<Bill, Long> {
    List<Bill> findByIsClosedFalse();
    List<Bill> findByIsClosedTrue();
}
