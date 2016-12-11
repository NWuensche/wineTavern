package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface to handle {@link Vintner}s.
 * @author Louis Wilke
 */

public interface VintnerManager extends SalespointRepository<Vintner, Long> {
    List<Vintner> findAllByOrderByPosition();
    Optional<Vintner> findByName(String name);
    List<Vintner> findAll();
}
