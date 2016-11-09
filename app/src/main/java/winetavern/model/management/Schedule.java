package winetavern.model.management;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */

@Entity
public class Schedule implements Comparable<Schedule> {
    @Transient @Autowired private ShiftRepository shiftRepository;

    @Id @GeneratedValue private long id;
    private int week;
    @ManyToMany private Set<Shift> shifts;

    public Schedule(int week) {
        if (week < 1 || week > 53) throw new IllegalArgumentException("illegal calendar week (after ISO-8601)");
        this.week = week;
    }

    public long getId() {
        return id;
    }

    public int getWeek() {
        return week;
    }

    public void addShift(Shift shift) {
        if (shiftRepository.exists(shift.getId())) throw new IllegalArgumentException("the shift already exists");
        shiftRepository.save(shift);
        shifts.add(shift);
    }

    public void removeShift(Shift shift) {
        shiftRepository.delete(shift);
        shifts.remove(shift);
    }

    public Set<Shift> getShiftsByInterval(TimeInterval i1) {
        Set<Shift> res = new HashSet<>();
        for (Shift shift : shifts) {
            TimeInterval i2 = shift.getInterval();
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
