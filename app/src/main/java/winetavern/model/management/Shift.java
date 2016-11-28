package winetavern.model.management;

import winetavern.model.user.Employee;

import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class Shift implements Comparable<Shift> {
    //@Transient @Autowired private EmployeeManager employeeManager;

    @Id @GeneratedValue private long id;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @ManyToOne private Employee worker;

    @Deprecated
    protected Shift() {}

    public Shift(TimeInterval interval, Employee worker) {
        setInterval(interval);
        setEmployee(worker);
    }

    public long getId() {
        return id;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public void setInterval(TimeInterval interval) {
        if (interval == null) throw new NullPointerException("the interval must not be null");
        this.interval = interval;
    }

    public Employee getEmployee() {
        return worker;
    }

    public void setEmployee(Employee worker) {
        if (worker == null) throw new NullPointerException("the worker must not be null");
        this.worker = worker;
    }

    @Override
    public int compareTo(Shift o) {
        return interval.getStart().compareTo(o.getInterval().getStart());
    }
}
