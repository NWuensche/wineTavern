package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;

import java.util.Optional;

/**
 * Repository interface to handle {@link External}s.
 * @author Niklas Wünsche
 */
public interface ExternalManager extends SalespointRepository<External, Long> {
    Optional<External> findByName(String name);
}
