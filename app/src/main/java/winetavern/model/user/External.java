package winetavern.model.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

/**
 * Entity for all (Groups of) Persons, that don't need a login to the Website (like artists)
 * @author Niklas Wünsche
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class External extends Person {
    private String name;
    private MonetaryAmount wage;

    public External(String name, MonetaryAmount wage) {
        this.name = name;
        this.wage = wage;
    }

    @Override
    public String toString() {
        return name;
    }
}
