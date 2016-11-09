package winetavern.model.management;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Louis
 */
@Entity
@Table(name = "EVENTS")
public class Event extends Product {
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    private String description;

    @Deprecated
    protected Event() {}

    public Event(String name, MonetaryAmount price, TimeInterval interval, String description) {
        super(name, price, Metric.UNIT);
        if (interval == null) throw new NullPointerException("the interval must not be null");
        if (description == null) throw new NullPointerException("the description must not be null");
        setInterval(interval);
        setDescription(description);
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public void setInterval(TimeInterval interval) {
        if (interval == null) throw new NullPointerException("the interval must not be null");
        this.interval = interval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) throw new NullPointerException("the description must not be null");
        this.description = description;
    }
}
