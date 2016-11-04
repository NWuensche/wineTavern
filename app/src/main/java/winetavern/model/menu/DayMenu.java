package kickstart.model.menu;

import org.salespointframework.catalog.Product;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.Id;
import org.springframework.data.repository.Repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import java.util.*;
/**
 * Created by Michel on 11/3/2016.
 */

interface DayMenuRepository extends Repository<DayMenu, Long> {
    List<DayMenu> findById(Long id);
    List<DayMenu> findByDay(Date day);
}

@Entity
public class DayMenu {

    @Id
    private long id;

    private Date day;
    @ManyToMany(targetEntity=MenuItem.class)
    private List<MenuItem> menuItems;

    public void setDay(Date day) {
        this.day = day;
    }

    public Date getDay() {
        return day;
    }

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    public void removeMenuItem(MenuItem menuItem) {
        menuItems.remove(menuItem);
    }
}
