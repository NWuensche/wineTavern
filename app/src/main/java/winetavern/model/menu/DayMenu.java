package winetavern.model.menu;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
/**
 * Created by Michel on 11/3/2016.
 */


@Entity
@Getter
public class DayMenu {

    @Id @GeneratedValue Long id;

    @Setter private Calendar day;

    @ManyToMany(fetch= FetchType.EAGER, targetEntity=DayMenuItem.class, mappedBy = "dayMenus")
    private List<DayMenuItem> dayMenuItems;

    @PreRemove
    private void removeDayMenusFromDayMenuItems() {
        for (DayMenuItem dayMenuItem : dayMenuItems) {
            dayMenuItem.getDayMenus().remove(this);
        }
    }

    public DayMenu() {}

    public DayMenu(Calendar day) {
        this.day = day;
    }

    public String getReadableDay() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(day.getTime());
    }

    public void addMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.add(dayMenuItem);
    }

    public void removeMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.remove(dayMenuItem);
    }

}
