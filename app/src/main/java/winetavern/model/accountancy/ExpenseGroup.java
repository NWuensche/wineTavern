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
    private String description;

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
