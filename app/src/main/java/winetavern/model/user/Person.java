package winetavern.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Super class for {@link External}s and {@link Employee}s
 * @author Niklas Wünsche
 */

@Entity
public abstract class Person {
    @GeneratedValue @Id private Long id;

    public Long getId() {
        return id;
    }
}