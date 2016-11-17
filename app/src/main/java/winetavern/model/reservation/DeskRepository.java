package winetavern.model.reservation;

import org.salespointframework.core.SalespointRepository;

import java.util.List;

/**
 * @author Sev, Michel
 */

public interface DeskRepository extends SalespointRepository<Desk, Long> {
    public List<Desk> findByCapacityGreaterThanEqualOrderByCapacity(Integer capacity);
    public Desk findByName(String name);
}
