package winetavern.model.management;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;
import org.salespointframework.time.Interval;

import javax.money.MonetaryAmount;

/**
 * @author Louis
 */
public class Event extends Product {
    private Interval interval;
    private String description;

    public Event(String name, MonetaryAmount price, Interval interval, String description) {
        super(name, price, Metric.UNIT);
        this.interval = interval;
        this.description = description;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
