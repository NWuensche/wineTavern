package winetavern.model.accountancy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Louis
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class ExpenseGroup {
    @GeneratedValue @Id private long id;
    @NonNull private String name;

    public ExpenseGroup(String name) {
        setName(name);
    }

    public void setName(@NonNull String name) {
        if (name.equals("")) throw new IllegalArgumentException("The name must not be empty");
        this.name = name;
    }
}
