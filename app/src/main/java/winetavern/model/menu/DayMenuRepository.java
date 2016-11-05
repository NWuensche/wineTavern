package winetavern.model.menu;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 11/5/2016.
 */
public interface DayMenuRepository extends CrudRepository<DayMenu, Long> {
    DayMenu findById(Long id);
    DayMenu findByDay(Calendar day);

}