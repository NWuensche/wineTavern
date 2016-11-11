package winetavern.model.menu;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by Michel on 11/3/2016.
 */

@Entity
public class DayMenu {

    @Id
    @GeneratedValue
    private long id;

    private Calendar day;
    @ManyToMany(targetEntity=DayMenuItem.class)
    private List<DayMenuItem> dayMenuItems;

    protected DayMenu() {}

    public DayMenu(Calendar day) {
        this.day = day;
    }

    public long getId() {
        return id;
    }

    public void setDay(Calendar day) {
        this.day = day;
    }

    public Calendar getDay() {
        return day;
    }

    public String getReadableDay() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(day.getTime());
    }

    public List<DayMenuItem> getDayMenuItems() {
        return dayMenuItems;
    }

    public void addMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.add(dayMenuItem);
    }

    public void removeMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.remove(dayMenuItem);
    }
}
