package winetavern.model.management;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;
import winetavern.model.user.External;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * @author Louis
 */

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class Event extends Product {
    @NonNull @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @NonNull @ManyToOne(cascade = {CascadeType.PERSIST}) private Person person;
    @NonNull private String description;
    private boolean vintnerDay = false;

    public Event(@NonNull String name, @NonNull MonetaryAmount price, @NonNull TimeInterval interval, @NonNull String description, @NonNull Person person) {
        super(name, price, Metric.UNIT);
        if (description.equals(""))
            throw new IllegalArgumentException("The description must not be empty");
        this.interval = interval;
        this.description = description;
        this.person = person;
    }

    public void setDescription(String description) {
        if(description.isEmpty()) throw new IllegalArgumentException("description");

        this.description = description;
    }
}
