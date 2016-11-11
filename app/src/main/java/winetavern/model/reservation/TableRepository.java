package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;

/**
 * @author Sev
 */

public interface TableRepository extends SalespointRepository<Table, Long> {
    public List<Table> findByCapacityGreaterThanEqual(Integer capacity);
}
