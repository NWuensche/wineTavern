package winetavern.model.menu;

import org.salespointframework.core.SalespointRepository;
import java.time.LocalDate;


/**
 * @author Michel
 */
public interface DayMenuRepository extends SalespointRepository<DayMenu, Long> {
    DayMenu findByDay(LocalDate day);
}