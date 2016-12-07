package winetavern.model.accountancy;

import lombok.NonNull;
import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Louis
 */

@Entity
public class Expense extends AccountancyEntry implements Comparable<Expense> {
    private boolean isCovered = false;
    @ManyToOne private Person person;
    @ManyToOne private ExpenseGroup expenseGroup;

    @Deprecated
    protected Expense() {}

    public Expense(MonetaryAmount value, String description, @NonNull Person person, @NonNull ExpenseGroup expenseGroup) {
        super(value, description);
        this.expenseGroup = expenseGroup;
        this.person = person;
        if (expenseGroup.getName().equals("Abrechnung")) isCovered = true;
    }

    public ExpenseGroup getExpenseGroup() {
        return expenseGroup;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void cover() {
        if (isCovered) throw new IllegalStateException("Expense is already covered");
        this.isCovered = true;
    }

    @Override
    public int compareTo(Expense o) {
        if (super.hasDate() && o.hasDate()) return -super.getDate().get().compareTo(o.getDate().get());
        return Long.compare(expenseGroup.getId(), o.getExpenseGroup().getId());
    }
}