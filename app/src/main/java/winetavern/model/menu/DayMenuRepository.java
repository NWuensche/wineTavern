package winetavern.model.menu;

import org.springframework.data.repository.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */
public interface DayMenuRepository extends Repository<DayMenu, Long> {
    List<DayMenu> findById(Long id);
    List<DayMenu> findByDay(Date day);
}