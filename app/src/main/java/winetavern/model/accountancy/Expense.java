package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;

/**
 * Created by Michel on 11/4/2016.
 */
@Entity
public class Expense extends AccountancyEntry {
    @ManyToOne(targetEntity = ExpenseGroup.class)
    private ExpenseGroup expenseGroup;

    private LocalDateTime matureDate;
}
