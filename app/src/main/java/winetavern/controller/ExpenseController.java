package winetavern.controller;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.javamoney.moneta.Money;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
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
import winetavern.model.user.*;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @NonNull @Autowired private PersonManager persons;
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

        Optional<String> groupId = !StringUtils.isBlank(group) ? Optional.of(group) : Optional.empty();
        Optional<String> personId = !StringUtils.isBlank(person) ? Optional.of(person) : Optional.empty();

        Set<Expense> expOpen = filter(groupId, personId, false, date); //all open expenses with the given filter
        Set<Expense> expCovered = filter(groupId, personId, true, date); //all covered expenses with the given filter

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
     * @param interval     the interval in which the expense must lay in
     *                 format: 'dd.MM.yyyy - dd.MM.yyyy'
     * @return Set<Expense> the set filled with all expense that 'passed' the filter criteria
     */
    private Set<Expense> filter(Optional<String> groupId, Optional<String> personId, boolean covered, String interval) {
        Set<Expense> res;

        String filterByInterval = getCurrentDayIfDateIsToday(interval);

        res = filterExpensesByInterval(filterByInterval);

        //ExpenseGroup filter: must contain expenseGroup
        groupId.ifPresent(id -> {
            ExpenseGroup expenseGroup = expenseGroups.findOne(Long.parseLong(id)).get();
            res.removeIf(expense -> expense.getExpenseGroup() != expenseGroup);
        });

        //Person filter: must contain person
        personId.ifPresent(id -> {
            Person person = persons.findOne(Long.parseLong(id)).get();
            res.removeIf(expense -> !expense.getPerson().getId().equals(person.getId()));
        });

        //isCovered filter: true -> returns only paid expenses
        res.removeIf(expense -> expense.isCovered() != covered);

        return res;
    }

    private String getCurrentDayIfDateIsToday(String date) {
        String rightFormattedDate;
        if (date.equals("today")) {
            String currentBusinessTime = bt.getTime().format(formatter);
            rightFormattedDate = currentBusinessTime + " - " + currentBusinessTime;
        } else {
            rightFormattedDate = date;
        }

        return rightFormattedDate;
    }
    
    private Set<Expense> filterExpensesByInterval(String interval) {
        Function<AccountancyEntry, Expense> castEntries = entry -> (Expense) entry;

        if(StringUtils.isBlank(interval)) {
            return accountancy.findAll()
                    .stream()
                    .map(castEntries)
                    .collect(Collectors.toSet());
        }

        String[] splitInterval = interval.split("(\\s-\\s)");
        LocalDateTime start = LocalDate.parse(splitInterval[0], formatter).atStartOfDay().withNano(1);
        LocalDateTime end = LocalDate.parse(splitInterval[1], formatter).atTime(23, 59, 59, 999999999);
        return accountancy.find(Interval.from(start).to(end))
                .stream()
                .map(castEntries)
                .collect(Collectors.toSet());
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
        Set<Expense> expenses = filter(Optional.of("Bestellung"),
                Optional.of(personId), false, "");
        model.addAttribute("expenses", expenses);
        model.addAttribute("staff", staff);

        MonetaryAmount sum = expenses
                .stream()
                .map(Expense::getValue)
                .reduce(Money.of(0, EURO), MonetaryAmount::add);

        model.addAttribute("price", sum);
        return "payoff";
    }

    private String idOfGroup(String groupName) {
        return "" + expenseGroups.findByName(groupName).get().getId();
    }

    @RequestMapping("/accountancy/expenses/payoff/{pid}/pay")
    public String coverExpensesForPerson(@PathVariable("pid") String personId) {
        Set<Expense> expenses = filter(Optional.of(idOfGroup("Bestellung")), Optional.of(personId), false, "");

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