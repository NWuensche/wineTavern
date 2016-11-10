package winetavern.model.management;

import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class Shift {
    @Transient @Autowired private PersonManager personManager;

    @Id @GeneratedValue private long id;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @OneToOne private Person worker;

    public Shift(TimeInterval interval, Person worker) {
        setInterval(interval);
        setPerson(worker);
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

    public Person getPerson() {
        return worker;
    }

    public void setPerson(Person worker) {
        if (worker == null) throw new NullPointerException("the worker must not be null");
        if (!personManager.exists(worker.getId()))
            throw new IllegalArgumentException("the person must be in the repository");
        this.worker = worker;
    }
}
