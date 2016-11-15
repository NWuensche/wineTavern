package winetavern.model.management;

import winetavern.model.user.Person;

import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class Shift implements Comparable<Shift> {
    //@Transient @Autowired private PersonManager personManager;

    @Id @GeneratedValue private long id;
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @ManyToOne private Person worker;

    @Deprecated
    protected Shift() {}

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
        this.worker = worker;
    }

    @Override
    public int compareTo(Shift o) {
        return interval.getStart().compareTo(o.getInterval().getStart());
    }
}
