package winetavern.model.menu;

import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by Michel on 11/5/2016.
 */
public interface DayMenuItemRepository extends Repository<DayMenuItem, Long> {
    List<DayMenuItem> findById(Long id);
    List<DayMenuItem> findAll();
}