package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.accountancy.AccountancyEntryIdentifier;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class ExpenseController {
    @NotNull @Autowired private Accountancy accountancy;
    @NotNull @Autowired private ExpenseGroupRepository expenseGroups;
    @NotNull @Autowired private EmployeeManager employees;
    @NotNull @Autowired private BusinessTime bt;
    @NotNull @Autowired private AuthenticationManager am;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(@ModelAttribute("type") String type, @ModelAttribute("person") String person,
                               @ModelAttribute("date") String date,
                               @ModelAttribute("cover") Optional<String> cover, Model model) {
        if (cover.isPresent()) { //RLY SALESPOIN?! NO STRING -> ID????
            Expense expense = null;
            MonetaryAmount sum = Money.of(0, EURO);
            String[] idQuery = cover.get().split("\\|"); //split into multiple ExpenseID's
            for (AccountancyEntry exp : accountancy.findAll()) {
                for (String expenseId : idQuery) {
                    if (expenseId.equals(exp.getId().toString())) {
                        expense = ((Expense) exp);
                        expense.cover();
                        accountancy.add(expense);
                        sum = sum.add(expense.getValue());
                    }
                }
            }
            Expense payoff = new Expense(sum,
                    "Abrechnung " + bt.getTime().format(formatter) + " durch " +
                            employees.findByUserAccount(am.getCurrentUser().get()).get(),
                    expense.getEmployee(),
                    expenseGroups.findByName("Abrechnung").get());
            accountancy.add(payoff);
            return "redirect:/accountancy/expenses";
        }

        if (type.equals(""))
            type = "0";
        if (person.equals(""))
            person = "0";
        Set<Expense> expOpen = filter(type, person, false, date);
        Set<Expense> expCovered = filter(type, person, true, date);
        model.addAttribute("expenseAmount", expOpen.size());
        model.addAttribute("expOpen", expOpen);
        model.addAttribute("expCovered", expCovered);
        model.addAttribute("persons", employees.findAll());
        model.addAttribute("groups", expenseGroups.findAll());
        model.addAttribute("selectedType", Long.parseLong(type));
        model.addAttribute("selectedEmployee", Long.parseLong(person));
        model.addAttribute("selectedDate", date);
        return "expenses";
    }

    @RequestMapping("/accountancy/expenses/payoff")
    public String doPayoff(Model model) {
        Set<Employee> service = new TreeSet<>(Comparator.comparing(o -> o.getUserAccount().getLastname()));
        employees.findAll().forEach(service::add);
        model.addAttribute("service", service);
        return "payoff";
    }

    @PostMapping("/accountancy/expenses/payoff")
    public String redirectPayoff(@ModelAttribute("personId") String personId) {
        return "redirect:/accountancy/expenses/payoff/" + personId;
    }

    @RequestMapping("/accountancy/expenses/payoff/{pid}")
    public String doPayoffForPerson(@PathVariable("pid") String personId, Model model) {
        Person staff = employees.findOne(Long.parseLong(personId)).get();
        Set<Expense> expenses = filter(""+expenseGroups.findByName("Bestellung").get().getId(),
                personId, false, "");
        model.addAttribute("expenses", expenses);
        model.addAttribute("staff", staff);
        MonetaryAmount sum = Money.of(0, EURO);
        for (Expense exp : expenses) {
            sum = sum.add(exp.getValue());
        }
        model.addAttribute("price", sum);
        return "payoff";
    }

    @RequestMapping("/accountancy/expenses/payoff/{pid}/pay")
    public String coverExpensesForPerson(@PathVariable("pid") String personId) {
        Set<Expense> expenses = filter(""+expenseGroups.findByName("Bestellung").get().getId(),
                personId, false, "");
        MonetaryAmount sum = Money.of(0, EURO);
        for(Expense expense : expenses){
            sum = sum.add(expense.getValue());
            expense.cover();
            accountancy.add(expense);
        }
        accountancy.add(new Expense(sum,
                "Tagesabrechnung " + bt.getTime().toLocalDate().format(formatter) + " durch " +
                        employees.findByUserAccount(am.getCurrentUser().get()).get(),
                employees.findOne(Long.parseLong(personId)).get(),
                expenseGroups.findByName("Abrechnung").get()));
        return "redirect:/accountancy/expenses/payoff";
    }

    private Set<Expense> filter(String typeId, String employeeId, boolean covered, String date) {
        Set<Expense> res = new TreeSet<>();

        if (date.equals("today")) { //Interval filter: today
            date = bt.getTime().format(formatter);
            date += " - " + date;
        }
        if (!date.equals("")) { //Interval filter: start - end
            String[] interval = date.split("(\\s-\\s)");
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

        if (!employeeId.equals("0")) { //Employee filter: must contain employee
            Employee employee = employees.findOne(Long.parseLong(employeeId)).get();
            res.removeIf(expense -> expense.getEmployee() != employee);
        }

        //isCovered filter: true -> returns only paid expenses
        res.removeIf(expense -> expense.isCovered() != covered);

        return res;
    }
}