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
