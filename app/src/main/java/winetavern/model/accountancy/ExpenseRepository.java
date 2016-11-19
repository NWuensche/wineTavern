package winetavern.model.accountancy;

import org.salespointframework.accountancy.Accountancy;
import winetavern.model.user.Person;

import java.util.List;

/**
 * @author Louis
 */

public interface ExpenseRepository extends Accountancy {
    List<Expense> findByExpenseGroup(ExpenseGroup expenseGroup);
    List<Expense> findByPerson(Person person);
    List<Expense> findByIsCoverdFalse();
    List<Expense> findByIsCoverdTrue();
    List<Expense> findByPersonAndIsCoveredFalse(Person person);
    List<Expense> findByPersonAndIsCoveredTrue(Person person);
}
