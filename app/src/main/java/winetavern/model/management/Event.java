package winetavern.model.management;

import lombok.*;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;
import winetavern.model.user.External;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * @author Louis
 */

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class Event extends Product{
    @NonNull @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @NonNull @ManyToOne(cascade = {CascadeType.ALL}) private External external;
    @NonNull private String description;

    public Event(String name, MonetaryAmount price, TimeInterval interval, String description, External external) {
        super(name, price, Metric.UNIT);
        if (description.equals(""))
            throw new IllegalArgumentException("The description must not be empty");
        this.interval = interval;
        this.description = description;
        this.external = external;
    }
}
