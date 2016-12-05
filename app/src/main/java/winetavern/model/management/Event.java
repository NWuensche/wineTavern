package winetavern.model.management;

import lombok.NonNull;
import org.hibernate.validator.constraints.NotEmpty;
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
public class Event extends Product{
    @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    private String description;

    @Deprecated
    protected Event() {}

    public Event(String name, MonetaryAmount price, @NonNull TimeInterval interval, @NonNull String description) {
        super(name, price, Metric.UNIT);
        if (description.equals(""))
            throw new IllegalArgumentException("The description must not be empty");
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
