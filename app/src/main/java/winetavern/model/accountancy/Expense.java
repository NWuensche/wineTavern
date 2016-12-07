package winetavern.model.accountancy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
public class Expense extends AccountancyEntry implements Comparable<Expense> {
    @Getter private boolean covered = false;
    @Getter @ManyToOne private Person person;
    @Getter @ManyToOne private ExpenseGroup expenseGroup;

    public Expense(MonetaryAmount value, String description, @NonNull Person person, @NonNull ExpenseGroup expenseGroup) {
        super(value, description);
        this.expenseGroup = expenseGroup;
        this.person = person;
        if (expenseGroup.getName().equals("Abrechnung")) covered = true;
    }

    public String getDateString() {
        return super.getDate().get().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public void cover() {
        if (covered) throw new IllegalStateException("Expense is already covered");
        this.covered = true;
    }

    @Override
    public int compareTo(Expense o) {
        if (super.hasDate() && o.hasDate()) return -super.getDate().get().compareTo(o.getDate().get());
        return Long.compare(expenseGroup.getId(), o.getExpenseGroup().getId());
    }
}