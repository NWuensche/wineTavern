package winetavern.model.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
/**
 * Created by Michel on 11/3/2016.
 */


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
@Getter
public class DayMenu {

    @Id @GeneratedValue Long id;

    @Setter private Calendar day;

    @ManyToMany(fetch= FetchType.EAGER, targetEntity=DayMenuItem.class, mappedBy = "dayMenus")
    private List<DayMenuItem> dayMenuItems;

    public DayMenu(Calendar day) {
        dayMenuItems = new ArrayList<>();

        this.day = day;
    }

    public String getReadableDay() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(day.getTime());
    }

    public void addMenuItem(DayMenuItem newItem) {
        dayMenuItems.add(newItem);
    }

    public void removeMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.remove(dayMenuItem);
    }

}
