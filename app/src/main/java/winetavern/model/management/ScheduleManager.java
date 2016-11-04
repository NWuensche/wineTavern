package winetavern.model.management;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class ScheduleManager {
    private static ScheduleManager instance;
    private Set<Schedule> schedules;

    public ScheduleManager() {
        this.schedules = new HashSet<>();
    }

    public static ScheduleManager getInstance() {
        if (ScheduleManager.instance == null)
            ScheduleManager.instance = new ScheduleManager();
        return ScheduleManager.instance;
    }

    public boolean addSchedule(Schedule schedule) {
        return schedules.add(schedule);
    }

    public boolean removeSchedule(Schedule schedule) {
        return schedules.remove(schedule);
    }

    public Schedule getSchedule(int week) {
        return null;
    }
}
