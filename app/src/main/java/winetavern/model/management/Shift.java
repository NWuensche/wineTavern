package winetavern.model.management;

import org.salespointframework.time.Interval;
import winetavern.model.user.Staff;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Louis
 */
public class Shift {
    private Interval interval;
    private Role role;
    private Staff worker;

    public Shift(Interval interval, Role role, Staff worker) {
        this.interval = interval;
        this.role = role;
        this.worker = worker;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Staff getWorker() {
        return worker;
    }

    public void setWorker(Staff worker) {
        this.worker = worker;
    }

    protected enum Role {
        SERVICE,
        COOK,
        ACCOUNTANT
    }
}
