package winetavern.model.management;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class Schedule {
    private int week;
    private Set<Shift> shifts;

    public Schedule(int week) {
        this.shifts = new HashSet<>();
        this.week = week;
    }

    public boolean addShift(Shift shift) {
        return shifts.add(shift);
    }

    public boolean removeShift(Shift shift) {
        return shifts.remove(shift);
    }

    public Set<Shift> getShiftsByTime(LocalDateTime time) {
        Set<Shift> res = new HashSet<>();
        for (Shift shift: shifts) {
            if (shift.getDate().compareTo(time) == 1) //TODO: check if shift is <= time + shift.duration
                res.add(shift);
        }
        return res;
    }
}
