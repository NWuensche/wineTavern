package winetavern.model.accountancy;

import org.salespointframework.core.SalespointRepository;

import java.util.LinkedList;

/**
 * @author Louis
 */

public interface BillItemRepository extends SalespointRepository<BillItem, Long> {
    LinkedList<BillItem> findByReadyFalse();
}