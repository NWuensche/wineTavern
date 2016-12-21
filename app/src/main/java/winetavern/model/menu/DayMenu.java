package winetavern.model.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michel
 */

@Entity
@Getter
public class DayMenu {

    @Id @GeneratedValue Long id;

    @Setter @NonNull private LocalDate day;

    @ManyToMany(fetch= FetchType.EAGER, targetEntity=DayMenuItem.class, mappedBy = "dayMenus")
    @Setter private List<DayMenuItem> dayMenuItems;

    public DayMenu(@NonNull LocalDate day) {
        dayMenuItems = new ArrayList<>();

        this.day = day;
    }

    public DayMenu() {
        dayMenuItems = new ArrayList<>();
    }

    public String getReadableDay() {
        return day.toString();
    }

    public void addMenuItem(DayMenuItem newItem) {
        dayMenuItems.add(newItem);
        newItem.addDayMenu(this);
    }

    public void removeMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.remove(dayMenuItem);
        dayMenuItem.removeDayMenu(this);
    }

}
