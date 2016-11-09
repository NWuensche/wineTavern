package winetavern.model.management;

import org.salespointframework.time.Interval;
import winetavern.model.user.Person;

/**
 * @author Louis
 */

public class Shift {
    private Interval interval;
    private Person worker;

    public Shift(Interval interval, Person worker) {
        setInterval(interval);
        setWorker(worker);
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        if (interval == null) throw new NullPointerException("the interval must not be null");
        this.interval = interval;
    }

    public Person getWorker() {
        return worker;
    }

    public void setWorker(Person worker) {
        if (worker == null) throw new NullPointerException("the worker must not be null");
        this.worker = worker;
    }
}
