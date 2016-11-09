package winetavern.model.menu;


import org.springframework.data.repository.CrudRepository;
import java.util.Calendar;


/**
 * Created by Michel on 11/5/2016.
 */
public interface DayMenuRepository extends CrudRepository<DayMenu, Long> {
    DayMenu findById(Long id);
    DayMenu findByDay(Calendar day);
}