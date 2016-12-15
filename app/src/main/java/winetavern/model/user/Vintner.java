package winetavern.model.user;

import lombok.*;
import org.assertj.core.util.Strings;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis Wilke
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class Vintner extends Person {
    private String name;
    private int position; //the position in the vintner evening sequence
    @OneToMany private Set<Product> wineSet = new HashSet<>();
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

    public boolean addWine(Product wine) {
        return wineSet.add(wine);
    }

    public boolean removeWine(Product wine) {
        return wineSet.remove(wine);
    }

    @Override
    public String toString() {
        return name;
    }
}
