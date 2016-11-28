package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;
import org.salespointframework.core.Streamable;

import java.util.Optional;

/**
 * Created by nwuensche on 28.11.16.
 */
public interface ExternalManager extends SalespointRepository<External, Long> {
    Optional<External> findByName(String name);
}
