package winetavern.model.menu;

import org.salespointframework.core.SalespointRepository;
import java.time.LocalDate;
import java.util.Optional;


/**
 * @author Michel
 */
public interface DayMenuRepository extends SalespointRepository<DayMenu, Long> {
    Optional<DayMenu> findByDay(LocalDate day);
}