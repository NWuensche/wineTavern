package winetavern.controller;

import org.salespointframework.accountancy.Accountancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Expense;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.accountancy.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Louis
 */

@Controller
public class ExpenseController {
    @Autowired private Accountancy accountancy;
    @Autowired private ExpenseGroupRepository expenseGroups;

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(Model model) {
        List<Expense> expensestoday = ExpenseRepository.findAll(accountancy);
        List<Expense> expensesold = ExpenseRepository.findAll(accountancy); //TODO findold and find todays
        model.addAttribute("expenseAmount", expensestoday.size());
        model.addAttribute("exptoday", expensestoday);
        model.addAttribute("expold",expensesold);
        return "expenses";
    }
}
