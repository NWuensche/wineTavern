package winetavern.controller;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Louis
 */

@Controller
public class ExpenseController {
    @Autowired private Accountancy accountancy;
    @Autowired private ExpenseGroupRepository expenseGroups;
    @Autowired private BusinessTime businessTime;
    @Autowired private PersonManager persons;

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(@ModelAttribute("type") String type, @ModelAttribute("person") String person,
                               @ModelAttribute("covered") Optional<String> covered, Model model) {
        if (type.equals("") || person.equals("")) {
            type = "0";
            person = "0";
        }
        Set<Expense> expensesToday = filter(true, type, person, covered);
        Set<Expense> expensesOld = filter(false, type, person, covered);
        model.addAttribute("expenseAmount", expensesToday.size());
        model.addAttribute("exptoday", expensesToday);
        model.addAttribute("expold",expensesOld);
        model.addAttribute("persons",persons.findAll());
        model.addAttribute("groups",expenseGroups.findAll());
        return "expenses";
    }

    private Set<Expense> filter(boolean getCurrent, String typeId, String personId, Optional<String> covered) {
        System.out.println(typeId + ", " + personId + ", " + covered.isPresent());
        Set<Expense> res = findAll();
        if (getCurrent)
            res.removeIf(expense -> expense.hasDate() && !expense.getDate().get().toLocalDate().isEqual(businessTime.getTime().toLocalDate()));
        else
            res.removeIf(expense -> expense.hasDate() && expense.getDate().get().toLocalDate().isEqual(businessTime.getTime().toLocalDate()));
        if (!typeId.equals("0")) {
            ExpenseGroup expenseGroup = expenseGroups.findOne(Long.parseLong(typeId)).get();
            res.removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        }
        if (!personId.equals("0")) {
            Person person = persons.findOne(Long.parseLong(personId)).get();
            res.removeIf(expense -> expense.getPerson() != person);
        }
        if (covered.isPresent())
            res.removeIf(expense -> !expense.isCovered());
        return res;
    }

    private Set<Expense> findAll() {
        Set<Expense> set = new TreeSet<>();
        accountancy.findAll().forEach(it -> set.add(((Expense) it)));
        return set;
    }
}
