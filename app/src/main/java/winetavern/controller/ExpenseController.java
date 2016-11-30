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
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;

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
    @NotNull private final EmployeeManager employees;

    @Autowired
    public ExpenseController(Accountancy accountancy, ExpenseGroupRepository expenseGroups, BusinessTime businessTime, EmployeeManager employees) {
        this.accountancy = accountancy;
        this.expenseGroups = expenseGroups;
        this.businessTime = businessTime;
        this.employees = employees;
    }

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(@ModelAttribute("type") String type, @ModelAttribute("person") String employee,
                               @ModelAttribute("date") String date, Model model) {
        if (type.equals("")) type = "0";
        if (employee.equals("")) employee = "0";
        Set<Expense> expopen = filter(type, employee, false, date);
        Set<Expense> expcovered = filter(type, employee, true, date);
        model.addAttribute("expenseAmount", expopen.size());
        model.addAttribute("expopen", expopen);
        model.addAttribute("expcovered", expcovered);
        model.addAttribute("employees", employees.findAll());
        model.addAttribute("groups", expenseGroups.findAll());
        model.addAttribute("selectedType", Long.parseLong(type));
        model.addAttribute("selectedEmployee", Long.parseLong(employee));
        model.addAttribute("selectedDate", date);
        return "expenses";
    }


    private Set<Expense> filter(String typeId, String employeeId, boolean covered, String date) {
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

        if (!employeeId.equals("0")) { //Employee filter: must contain employee
            Employee employee = employees.findOne(Long.parseLong(employeeId)).get();
            res.removeIf(expense -> expense.getEmployee() != employee);
        }

        if(covered){ //isCovered filter: true -> returns only paid expenses
            res.removeIf(expense -> !expense.isCovered());
        }

        return res;
    }
}