package winetavern.model.management;

import org.salespointframework.time.Interval;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class Schedule implements Comparable<Schedule> {
    private int week;
    private Set<Shift> shifts;

    public Schedule(int week) {
        this.shifts = new HashSet<>();
        this.week = week;
    }

    public int getWeek() {
        return week;
    }

    public boolean addShift(Shift shift) {
        return shifts.add(shift);
    }

    public boolean removeShift(Shift shift) {
        return shifts.remove(shift);
    }

    public Set<Shift> getShiftsByInterval(Interval i1) {
        Set<Shift> res = new HashSet<>();
        for (Shift shift : shifts) {
            Interval i2 = shift.getInterval();
            if (i2.getStart().compareTo(i1.getStart()) == 1 || //if a part of the shift lies in the interval i1
                    i2.getEnd().compareTo(i1.getEnd()) == -1)
                res.add(shift);
        }
        return res;
    }

    @Override
    public int compareTo(Schedule o) {
        return Integer.compare(week, o.getWeek());
    }
}
