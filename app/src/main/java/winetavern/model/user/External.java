package winetavern.model.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

/**
 * @author Niklas Wünsche
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class External extends Person {
    private String name;
    private MonetaryAmount wage;

    public External(@NonNull String name, @NonNull MonetaryAmount wage) {
        if (name.equals(""))
            throw new IllegalArgumentException("the name of an external must not be empty");
        this.name = name;
        this.wage = wage;
    }

    @Override
    public String toString() {
        return name;
    }
}
