package winetavern.model.accountancy;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Michel on 11/4/2016.
 */
@Entity
public class ExpenseGroup {
    @Id
    private long id;

    private String description;
}
