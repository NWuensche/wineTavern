package winetavern.model.accountancy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Louis
 */

@Entity
public class ExpenseGroup {
    @GeneratedValue @Id private long id;
    private String name;

    public ExpenseGroup(String name) {
        setName(name);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String description) {
        if (name == null) throw new NullPointerException();
        if (name.equals("")) throw new IllegalArgumentException("The name must not be empty");
        this.name = description;
    }
}
