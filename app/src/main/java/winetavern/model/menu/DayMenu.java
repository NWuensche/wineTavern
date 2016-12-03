package winetavern.model.menu;

import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    @Setter @NonNull private LocalDate day;

    @ManyToMany(fetch= FetchType.EAGER, targetEntity=DayMenuItem.class, mappedBy = "dayMenus")
    private List<DayMenuItem> dayMenuItems;

    public DayMenu(@NonNull LocalDate day) {
        dayMenuItems = new ArrayList<>();

        this.day = day;
    }

    public String getReadableDay() {
        return day.toString();
    }

    public void addMenuItem(DayMenuItem newItem) {
        dayMenuItems.add(newItem);
    }

    public void removeMenuItem(DayMenuItem dayMenuItem) {
        dayMenuItems.remove(dayMenuItem);
    }

}
