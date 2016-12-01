package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.core.Streamable;

import java.util.Optional;

/**
 * Repository interface to handle {@link External}s.
 * @author Niklas Wünsche
 */

public interface ExternalManager extends SalespointRepository<External, Long> {
    Optional<External> findByName(String name);
}
