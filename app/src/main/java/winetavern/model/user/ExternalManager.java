package winetavern.model.user;

import winetavern.ExtraRepository;

import java.util.Optional;

/**
 * Repository interface to handle {@link External}s.
 * @author Niklas Wünsche
 */
public interface ExternalManager extends ExtraRepository<External, Long> {
    Optional<External> findByName(String name);
}
