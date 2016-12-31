package winetavern.model.menu;

import org.salespointframework.core.SalespointRepository;
import winetavern.ExtraRepository;

/**
 * @author Michel
 */
public interface DayMenuItemRepository extends ExtraRepository<DayMenuItem, Long> {
    Iterable<DayMenuItem> findByEnabled(Boolean enabled);
}