package winetavern.model.menu;

import org.salespointframework.core.SalespointRepository;
import winetavern.ExtraRepository;

import java.time.LocalDate;
import java.util.Optional;


/**
 * @author Michel
 */
public interface DayMenuRepository extends ExtraRepository<DayMenu, Long> {
    Optional<DayMenu> findByDay(LocalDate day);
}