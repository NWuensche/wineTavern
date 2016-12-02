package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.user.Employee;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Louis
 */

@Entity
public class Expense extends AccountancyEntry implements Comparable<Expense> {
    private boolean isCovered = false;
    @ManyToOne private Employee employee;
    @ManyToOne private ExpenseGroup expenseGroup;

    @Deprecated
    protected Expense() {}

    public Expense(MonetaryAmount value, String description, Employee employee, ExpenseGroup expenseGroup) {
        super(value, description);
        if (employee == null || expenseGroup == null) throw new NullPointerException("no null parameter accepted here");
        this.expenseGroup = expenseGroup;
        this.employee = employee;
        if (expenseGroup.getName().equals("Abrechnung")) isCovered = true;
    }

    public ExpenseGroup getExpenseGroup() {
        return expenseGroup;
    }

    public Employee getEmployee() {
        return employee;
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
        return getEmployee().getUserAccount().getLastname().compareTo(o.getEmployee().getUserAccount().getLastname());
    }
}