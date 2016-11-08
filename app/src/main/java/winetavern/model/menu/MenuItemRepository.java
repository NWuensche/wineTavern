package winetavern.model.menu;

import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */
public interface MenuItemRepository extends Repository<MenuItem, Long> {
    List<MenuItem> findById(Long id);
    List<MenuItem> findAll();
}