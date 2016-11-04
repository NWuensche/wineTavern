package winetavern.model.management;

import winetavern.model.user.Staff;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Louis
 */
public class Shift {
    private LocalDateTime date;
    private Duration duration;
    private Role role;
    private Staff worker;

    public Shift(LocalDateTime date, Duration duration, Role role, Staff worker) {
        this.date = date;
        this.duration = duration;
        this.role = role;
        this.worker = worker;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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
