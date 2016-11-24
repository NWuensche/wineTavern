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

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Louis
 */

@Controller
public class ExpenseController {
    @NotNull private final Accountancy accountancy;
    @NotNull private final ExpenseGroupRepository expenseGroups;
    @NotNull private final BusinessTime businessTime;
    @NotNull private final PersonManager persons;

    @Autowired
    public ExpenseController(Accountancy accountancy, ExpenseGroupRepository expenseGroups, BusinessTime businessTime, PersonManager persons) {
        this.accountancy = accountancy;
        this.expenseGroups = expenseGroups;
        this.businessTime = businessTime;
        this.persons = persons;
    }

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(@ModelAttribute("type") String type, @ModelAttribute("person") String person, Model
            model) {
        if (type.equals("")) type = "0";
        if (person.equals("")) person = "0";
        Set<Expense> expopen = filter(type, person, false);
        Set<Expense> expcovered = filter(type, person, true);
        model.addAttribute("expenseAmount", expopen.size());
        model.addAttribute("expopen", expopen);
        model.addAttribute("expcovered",expcovered);
        model.addAttribute("persons",persons.findAll());
        model.addAttribute("groups",expenseGroups.findAll());
        return "expenses";
    }

    private Set<Expense> filter(String typeId, String personId, boolean covered) {
        Set<Expense> res = findAll();
        /*
        if (getCurrent)
            res.removeIf(expense -> expense.hasDate() && !expense.getDate().get().toLocalDate().isEqual(businessTime.getTime().toLocalDate()));
        else
            res.removeIf(expense -> expense.hasDate() && expense.getDate().get().toLocalDate().isEqual(businessTime.getTime().toLocalDate()));
            */
        //TODO date
        if (!typeId.equals("0")) {
            ExpenseGroup expenseGroup = expenseGroups.findOne(Long.parseLong(typeId)).get();
            res.removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        }
        if (!personId.equals("0")) {
            Person person = persons.findOne(Long.parseLong(personId)).get();
            res.removeIf(expense -> expense.getPerson() != person);
        }
        if(covered){
            res.removeIf(expense -> !expense.isCovered());
        }
        return res;
    }

    private Set<Expense> findAll() {
        Set<Expense> set = new TreeSet<>();
        accountancy.findAll().forEach(it -> set.add(((Expense) it)));
        return set;
    }
}