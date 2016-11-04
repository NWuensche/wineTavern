package winetavern.model.management;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Louis
 */
public class ScheduleManager {
    private static ScheduleManager instance;
    private Set<Schedule> schedules;

    public ScheduleManager() {
        this.schedules = new TreeSet<>();
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

    public Optional<Schedule> getSchedule(int week) {
        for (Schedule schedule : schedules)
            if (schedule.getWeek() == week) return Optional.of(schedule);
        return Optional.empty();
    }
}
