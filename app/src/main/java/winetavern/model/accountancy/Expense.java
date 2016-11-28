package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.format.DateTimeFormatter;

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

    public Expense(MonetaryAmount value, Person person, ExpenseGroup expenseGroup) {
        super(value);
        if (person == null || expenseGroup == null) throw new NullPointerException("no null parameter accepted here");
        this.expenseGroup = expenseGroup;
        this.person = person;
        if (expenseGroup.getName().equals("Abrechnung")) isCovered = true;
    }

    public Expense(MonetaryAmount value, String description, Person person, ExpenseGroup expenseGroup) {
        super(value, description);
        if (person == null || expenseGroup == null) throw new NullPointerException("no null parameter accepted here");
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

    public String getDateString() {
        return super.getDate().get().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
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
        return getPerson().getUserAccount().getLastname().compareTo(o.getPerson().getUserAccount().getLastname());
    }
}