package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Louis
 */

@Entity
public class Expense extends AccountancyEntry {
    @Transient @Autowired private BusinessTime businessTime;

    @ManyToOne private Person person;
    @ManyToOne private ExpenseGroup expenseGroup;

    @Deprecated
    protected Expense() {}

    public Expense(MonetaryAmount value, Person person, ExpenseGroup expenseGroup) {
        super(value);
        if (person == null || expenseGroup == null) throw new NullPointerException("no null parameter accepted here");
        this.expenseGroup = expenseGroup;
        this.person = person;
    }

    public Expense(MonetaryAmount value, String description, Person person, ExpenseGroup expenseGroup) {
        super(value, description);
        if (person == null || expenseGroup == null) throw new NullPointerException("no null parameter accepted here");
        this.expenseGroup = expenseGroup;
        this.person = person;
    }

    public ExpenseGroup getExpenseGroup() {
        return expenseGroup;
    }

    public Person getPerson() {
        return person;
    }
}
