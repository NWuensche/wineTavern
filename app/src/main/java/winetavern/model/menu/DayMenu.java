package winetavern.model.menu;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
/**
 * Created by Michel on 11/3/2016.
 */


@Entity
public class DayMenu {

    @Id @GeneratedValue Long id;

    private Calendar day;

    @OneToMany(fetch=FetchType.EAGER, targetEntity=DayMenuItem.class, cascade=CascadeType.ALL, mappedBy="dayMenu")
    private List<DayMenuItem> dayMenuItems;

    public DayMenu() {}

    public DayMenu(Calendar day) {
        this.day = day;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
