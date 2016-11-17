package winetavern.model.accountancy;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.order.Order;

import java.util.Optional;

/**
 * @author Louis
 */

public interface BillRepository extends SalespointRepository<Bill, Long> {
    Optional<Bill> findByOrder(Order order);
}
