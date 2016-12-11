package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Repository interface to handle {@link Vintner}s.
 * @author Louis Wilke
 */

public interface VintnerManager extends SalespointRepository<Vintner, Long> {
    LinkedList<Vintner> findByActiveTrueOrderByPosition();
    Optional<Vintner> findByName(String name);
    ArrayList<Vintner> findByActiveTrue();
    ArrayList<Vintner> findAll();
}
