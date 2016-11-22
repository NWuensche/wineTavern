package winetavern.model.accountancy;

import org.salespointframework.accountancy.Accountancy;
import winetavern.model.user.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Louis
 */

public class ExpenseRepository {
    public static List<Expense> findAll(Accountancy accountancy) {
        List<Expense> res = new ArrayList<>();
        accountancy.findAll().forEach(it -> res.add(((Expense) it)));
        return res;
    }

    public static List<Expense> findByExpenseGroup(Accountancy accountancy, ExpenseGroup expenseGroup) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        return res;
    }

    public static List<Expense> findByPerson(Accountancy accountancy, Person person) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> expense.getPerson() != person);
        return res;    }

    public static List<Expense> findByIsCoverdFalse(Accountancy accountancy) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> expense.isCovered());
        return res;    }

    public static List<Expense> findByIsCoverdTrue(Accountancy accountancy) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> !expense.isCovered());
        return res;    }

    public static List<Expense> findByPersonAndIsCoveredFalse(Accountancy accountancy, Person person) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> expense.isCovered() || expense.getPerson() != person);
        return res;    }

    public static List<Expense> findByPersonAndIsCoveredTrue(Accountancy accountancy, Person person) {
        List<Expense> res = new ArrayList<>();
        findAll(accountancy).removeIf(expense -> !expense.isCovered() || expense.getPerson() != person);
        return res;    }
}
