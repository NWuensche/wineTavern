package winetavern.model.management;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * @author Louis
 */
@Entity
public class Event extends Product {
    @OneToOne private TimeInterval interval;
    private String description;

    @Deprecated
    protected Event() {}

    public Event(String name, MonetaryAmount price, TimeInterval interval, String description) {
        super(name, price, Metric.UNIT);
        this.interval = interval;
        this.description = description;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public void setInterval(TimeInterval interval) {
        this.interval = interval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
