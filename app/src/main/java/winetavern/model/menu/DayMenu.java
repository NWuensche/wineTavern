package winetavern.model.menu;

import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.*;
/**
 * Created by Michel on 11/3/2016.
 */

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
