package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sev
 */

public interface TableRepository extends SalespointRepository<Table, Long> {
    public List<Table> findByCapacityGreaterThanEqualOrderByCapacity(Integer capacity);
    public List<Table> findByNumber(Integer number);
}
