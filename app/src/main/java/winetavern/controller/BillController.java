package winetavern.controller;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import winetavern.Helper;
import winetavern.model.accountancy.*;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.user.EmployeeManager;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Louis
 */

@Controller
public class BillController {
    @NotNull private final BillRepository bills;
    @NotNull private final BillItemRepository billItems;
    @NotNull private final AuthenticationManager authenticationManager;
    @NotNull private final EmployeeManager employees;
    @NotNull private final DayMenuItemRepository dayMenuItems;
    @NotNull private final DeskRepository tables;
    @NotNull private final Accountancy accountancy;
    @NotNull private final ExpenseGroupRepository expenseGroups;
    @NotNull private final BusinessTime businessTime;

    @Autowired
    public BillController(BillRepository bills, BillItemRepository billItems, AuthenticationManager authenticationManager, EmployeeManager employees, DayMenuItemRepository dayMenuItems, DeskRepository tables, Accountancy accountancy, ExpenseGroupRepository expenseGroups, BusinessTime businessTime) {
        this.bills = bills;
        this.billItems = billItems;
        this.authenticationManager = authenticationManager;
        this.employees = employees;
        this.dayMenuItems = dayMenuItems;
        this.tables = tables;
        this.accountancy = accountancy;
        this.expenseGroups = expenseGroups;
        this.businessTime = businessTime;
    }

    @RequestMapping("/service/bills")
    public String showBills(Model model){
        model.addAttribute("active", bills.findByIsClosedFalse());
        model.addAttribute("old", bills.findByIsClosedTrue());
        model.addAttribute("tables",tables.findAll());
        return "bills";
    }

    @RequestMapping("/service/bills/add")
    public String addBill(@ModelAttribute("table") String desk) {
        if(desk.equals("")){return "redirect:/service/bills";}
        Bill bill = new Bill(desk, employees.findByUserAccount(authenticationManager.getCurrentUser().get()).get());
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

    @RequestMapping(value="/service/bills/details/{billid}/add",method = RequestMethod.POST)
    public String addProductToBill(@PathVariable("billid") Long billid, @RequestParam("itemid") Long productid,
                                   @RequestParam("quantity") Integer quantity) {
        Bill bill = bills.findOne(billid).get();
        DayMenuItem item = dayMenuItems.findOne(productid).get();
        BillItem billItem = new BillItem(item);
        changeBillItem(bill, billItem, quantity);
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

    /**
     * @param query Format: billItemID,newNumberOfItems|billItemId,...
     */
    @RequestMapping(value = "/service/bills/details/{billid}",method = RequestMethod.GET)
    public String showBillDetails(@PathVariable("billid") Long billid, @ModelAttribute("save") Optional<String> query, Model model) {
        Bill bill = bills.findOne(billid).get();
        if (query.isPresent() && !query.equals("")) {
            Map<BillItem, Integer> args = queryToMap(query.get()); //split bill in billItemId,quantity
            for (BillItem billItem : args.keySet()) {
                changeBillItem(bill, billItem, args.get(billItem));
            }
            bills.save(bill);
            return "redirect:/service/bills/details/" + billid;
        } else {
            model.addAttribute("bill", bill);
            List<DayMenuItem> menuItems = Helper.convertToList(dayMenuItems.findAll());
            bill.getItems().forEach(it -> menuItems.remove(it.getItem()));
            model.addAttribute("menuitems", menuItems); //TODO show only items of the day
            return "billdetails";
        }
    }

    @RequestMapping("/service/bills/details/{billid}/print")
    public String printBill(@PathVariable("billid") Long billid, Model model){
        Bill bill = bills.findOne(billid).get();
        if (!bill.isClosed()) {
            bill.close(businessTime);
            bills.save(bill);
        }
        model.addAttribute("bill", bill);
        return "printbill";
    }

    /**
     * @param query Format: billItemID,numberOfRemainingItems|billItemId,...
     */
    @RequestMapping("/service/bills/details/{billid}/split")
    public String splitBill(@PathVariable("billid") Long billid, @ModelAttribute("query") Optional<String> query, Model model){
        Bill bill = bills.findOne(billid).get();
        if(query.isPresent()) {
            Bill newBill = new Bill(bill.getDesk(), bill.getStaff());
            bills.save(newBill);
            Map<BillItem, Integer> args = queryToMap(query.get()); //split bill in billItemId,quantity
            for (BillItem billItem : bill.getItems()) { //work with every item that existed before
                if (!args.keySet().contains(billItem)) {
                    bill.changeItem(billItem, 0);
                    newBill.changeItem(billItem, billItem.getQuantity());
                } else {
                    newBill.changeItem(new BillItem(billItem.getItem()), (billItem.getQuantity() - args.get(billItem)));
                    bills.save(newBill);
                    bill.changeItem(billItem, args.get(billItem));
                }
            }
            bills.save(bill);
            bills.save(newBill);
            model.addAttribute("leftbill",bill);
            model.addAttribute("rightbill",newBill);
            return "splitbill";

        } else {
            model.addAttribute("bill", bill);
            return "splitbill";
        }
    }

    private Map<BillItem, Integer> queryToMap(String query) {
        Map<BillItem, Integer> res = new HashMap<>();
        String[] args = query.substring(0, query.length() - 1).split("\\|"); //split bill in arguments
        for (String arg : args) { //split bill in billItemId,quantity
            String[] itemString = arg.split(",");
            res.put(billItems.findOne(Long.parseLong(itemString[0])).get(), Integer.parseInt(itemString[1]));
        }
        return res;
    }

    private void changeBillItem(Bill bill, BillItem billItem, int quantity) {
        int diff = quantity - billItem.getQuantity();
        if (diff > 0) { //adds orders to expenses of staff
            accountancy.add( //the staff pays 90% of the selling price
                    new Expense(billItem.getItem().getPrice().multiply(diff).multiply(0.9).negate(),
                    "Rechnung Nr. " + bill.getId() + ": " + diff + " x " + billItem.getItem().getName(),
                            employees.findByUserAccount(authenticationManager.getCurrentUser().get()).get(),
                    expenseGroups.findByName("Bestellung").get())
            );
        }
        bill.changeItem(billItem, quantity);
    }
}
