package winetavern.model.user;

import lombok.*;
import org.assertj.core.util.Strings;

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
    @Setter private boolean active;

    public Vintner(@NonNull String name, int position) {
        if(Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("name");
        }
        this.name = name;
        if (position < 0) //maybe lombok?
            throw new IllegalArgumentException("the position in the vintner evening sequence must not be negative");
        this.position = position;
        this.active = true;
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
