package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author Louis
 */

@Entity
public class Expense extends AccountancyEntry {
    @ManyToOne private ExpenseGroup expenseGroup;
    private LocalDateTime matureDate;

    public Expense(MonetaryAmount value, ExpenseGroup expenseGroup, LocalDateTime matureDate) {
        super(value);
        this.expenseGroup = expenseGroup;
        this.matureDate = matureDate;
    }

    public Expense(MonetaryAmount value, String description, ExpenseGroup expenseGroup, LocalDateTime matureDate) {
        super(value, description);
        this.expenseGroup = expenseGroup;
        this.matureDate = matureDate;
    }
}
