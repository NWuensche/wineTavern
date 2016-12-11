package winetavern.model.user;

import lombok.*;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

/**
 * @author Louis Wilke
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class Vintner extends Person {
    private String name;
    private int position; //the position in the vintner evening sequence

    public Vintner(@NonNull String name, int position) {
        this.name = name;
        if (position < 0) //maybe lombok?
            throw new IllegalArgumentException("the position in the vintner evening sequence must not be negative");
        this.position = position;
    }

    public void setPosition(int position) {
        if (position < 0) //maybe lombok?
            throw new IllegalArgumentException("the position in the vintner evening sequence must not be negative");
        this.position = position;
    }

    @Override
    public String toString() {
        return name;
    }
}
