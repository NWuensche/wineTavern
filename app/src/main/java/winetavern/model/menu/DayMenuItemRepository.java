package winetavern.model.menu;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by Michel on 11/5/2016.
 */
public interface DayMenuItemRepository extends CrudRepository<DayMenuItem, Long> {
    //Iterable<DayMenuItem> findByDayMenusNotIn(DayMenu dayMenu); // not working properly :`(
}