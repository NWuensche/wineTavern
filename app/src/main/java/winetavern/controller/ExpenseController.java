package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private ExpenseGroupRepository expenseGroups;

    @RequestMapping("/accountancy/expenses")
    public String showExpenses(Model model) {
        List<Expense> expenseList = new ArrayList<>();
        //expenses.findAll().forEach(it -> expenseList.add((Expense) it));
        model.addAttribute("expenseAmount", expenseList.size());
        model.addAttribute("expenses", expenseList);
        return "expenses";
    }
}
