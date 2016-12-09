package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;

import java.util.Optional;

/**
 * Repository interface to handle {@link Vintner}s.
 * @author Louis Wilke
 */

public interface VintnerManager extends SalespointRepository<Vintner, Long> {
    Optional<Vintner> findByName(String name);
}
