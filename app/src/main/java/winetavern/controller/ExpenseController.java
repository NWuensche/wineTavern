package winetavern.controller;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.javamoney.moneta.Money;
import org.salespointframework.accountancy.Accountancy;
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
import winetavern.Helper;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.ExternalManager;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class ExpenseController {
    @NonNull @Autowired private Accountancy accountancy;
    @NonNull @Autowired private ExpenseGroupRepository expenseGroups;
    @NonNull @Autowired private EmployeeManager employees;
    @NonNull @Autowired private ExternalManager externals;
    @NonNull @Autowired private BusinessTime bt;
    @NonNull @Autowired private AuthenticationManager am;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * @param type   the ExpenseGroup (long id) to filter with (only remain expenses with this group)
     * @see          ExpenseGroup
     * @param person the Person (long id) to filter with
     * @see          Person
     * @param date   the interval in which the expense must lay in
     *               format: 'dd.MM.yyyy - dd.MM.yyyy'
     * @param cover  if present: cover multiple open expenses (the user checked the checkbox of at least one expense)
     *               format:     'expenseID|expenseId|...|'
     */
    @RequestMapping("/accountancy/expenses")
    public String showExpenses(@ModelAttribute(value="type") String type, @ModelAttribute(value="person") String person,
                               @ModelAttribute(value="date") String date,
                               @ModelAttribute(value="cover") Optional<String> cover, Model model) {
        if (cover.isPresent()) { //the query of expenses to pay off is not empty

            String[] idQuery = cover.get().split("\\|"); //split into multiple ExpenseID's
            Pair<Expense, MonetaryAmount> result = coverExpenses(idQuery); //the last expense and sum (@see method)

            Expense payoff = new Expense(result.getValue(),
                    "Abrechnung " + bt.getTime().format(formatter) + " durch " +
                            employees.findByUserAccount(am.getCurrentUser().get()).get(),
                    result.getKey().getPerson(),
                    expenseGroups.findByName("Abrechnung").get());

            accountancy.add(payoff);
        }

        if (type.equals("")) //the type will be parsed in long, so it should be a number
            type = "0";
        if (person.equals(""))
            person = "0";

        Set<Expense> expOpen = filter(type, person, false, date); //all open expenses with the given filter
        Set<Expense> expCovered = filter(type, person, true, date); //all covered expenses with the given filter

        model.addAttribute("expOpenAmount", expOpen.size());
        model.addAttribute("expCoveredAmount", expCovered.size());
        model.addAttribute("expOpen", expOpen);
        model.addAttribute("expCovered", expCovered);
        model.addAttribute("persons", Helper.findAllPersons(employees, externals)); //all employees and externals
        model.addAttribute("groups", expenseGroups.findAll());
        model.addAttribute("selectedType", Long.parseLong(type));
        model.addAttribute("selectedPerson", Long.parseLong(person));
        model.addAttribute("selectedDate", date);
        return "expenses";
    }

    /**
     * @param expensesToCover the array with expenses to cover given by each String expenseId
     * @return a pair <k, v> with:
     *         Expense k: the expense which was covered last or null if the array was empty
     *         MonetaryAmount v: the sum of all values from the expenses
     */
    private Pair<Expense, MonetaryAmount> coverExpenses(String[] expensesToCover) {
        Expense expense = null;
        MonetaryAmount sum = Money.of(0, EURO);

        for (String expenseId : expensesToCover) {
            expense = Helper.findOne(expenseId, accountancy);
            expense.cover();
            accountancy.add(expense);
            sum = sum.add(expense.getValue());
        }

        return new ImmutablePair<>(expense, sum);
    }

    /**
     * @see Expense
     * @param typeId   the ExpenseGroup (long id) to filter with (only remain expenses with this group)
     * @see            ExpenseGroup
     * @param personId the Person (long id) to filter with
     * @see            Person
     * @param covered  true: only return expenses which are covered
     *                 false: only return open expenses
     * @param date     the interval in which the expense must lay in
     *                 format: 'dd.MM.yyyy - dd.MM.yyyy'
     * @return Set<Expense> the set filled with all expense that 'passed' the filter criteria
     */
    private Set<Expense> filter(String typeId, String personId, boolean covered, String date) {
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
        } else { //no filter
            accountancy.findAll().forEach(it -> res.add(((Expense) it)));
        }

        if (!typeId.equals("0")) { //ExpenseGroup filter: must contain expenseGroup
            ExpenseGroup expenseGroup = expenseGroups.findOne(Long.parseLong(typeId)).get();
            res.removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        }

        if (!personId.equals("0")) { //Person filter: must contain person
            Person person = Helper.findOnePerson(Long.parseLong(personId), employees, externals).get();
            res.removeIf(expense -> !expense.getPerson().getId().equals(person.getId()));
        }

        //isCovered filter: true -> returns only paid expenses
        res.removeIf(expense -> expense.isCovered() != covered);

        return res;
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
        Employee staff = employees.findOne(Long.parseLong(personId)).get();
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
        Set<Expense> expenses = filter("" + expenseGroups.findByName("Bestellung").get().getId(),
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
}