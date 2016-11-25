package winetavern.controller;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public String showExpenses(@ModelAttribute("type") String type, @ModelAttribute("person") String person,
                               @ModelAttribute("date") String date, Model model) {
        if (type.equals("")) type = "0";
        if (person.equals("")) person = "0";
        Set<Expense> expOpen = filter(type, person, false, date);
        Set<Expense> expCovered = filter(type, person, true, date);
        model.addAttribute("expenseAmount", expOpen.size());
        model.addAttribute("expOpen", expOpen);
        model.addAttribute("expCovered", expCovered);
        model.addAttribute("persons", persons.findAll());
        model.addAttribute("groups", expenseGroups.findAll());
        model.addAttribute("selectedType", Long.parseLong(type));
        model.addAttribute("selectedPerson", Long.parseLong(person));
        model.addAttribute("selectedDate", date);
        return "expenses";
    }

    @RequestMapping("/accountancy/expenses/payoff")
    public String doPayoff(Model model) {
        return "payoff";
    }

    private Set<Expense> filter(String typeId, String personId, boolean covered, String date) {
        Set<Expense> res = new TreeSet<>();

        if (!date.equals("")) { //Interval filter: start - end
            String[] interval = date.split("(\\s-\\s)");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime start = LocalDate.parse(interval[0], formatter).atStartOfDay().withNano(1);
            LocalDateTime end = LocalDate.parse(interval[1], formatter).atTime(23, 59, 59, 999999999);
            accountancy.find(Interval.from(start).to(end)).forEach(it -> res.add(((Expense) it)));
        } else {
            accountancy.findAll().forEach(it -> res.add(((Expense) it)));
        }

        if (!typeId.equals("0")) { //ExpenseGroup filter: must contain expenseGroup
            ExpenseGroup expenseGroup = expenseGroups.findOne(Long.parseLong(typeId)).get();
            res.removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        }

        if (!personId.equals("0")) { //Person filter: must contain person
            Person person = persons.findOne(Long.parseLong(personId)).get();
            res.removeIf(expense -> expense.getPerson() != person);
        }

        if(covered){ //isCovered filter: true -> returns only paid expenses
            res.removeIf(expense -> !expense.isCovered());
        }

        return res;
    }
}