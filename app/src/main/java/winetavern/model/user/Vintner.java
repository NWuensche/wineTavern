package winetavern.model.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    public Vintner(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
