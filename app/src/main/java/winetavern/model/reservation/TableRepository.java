package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sev, Michel
 */

public interface TableRepository extends SalespointRepository<Table, Long> {
    public List<Table> findByCapacityGreaterThanEqualOrderByCapacity(Integer capacity);
    public Table findByName(String name);
}
