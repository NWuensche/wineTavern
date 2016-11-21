package winetavern.controller;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.model.accountancy.*;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.user.PersonManager;

import java.util.*;

/**
 * @author Louis
 */

@Controller
public class BillController {
    @Autowired private BillRepository bills;
    @Autowired private BillItemRepository billItems;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private PersonManager persons;
    @Autowired private DayMenuItemRepository dayMenuItems;
    @Autowired private DeskRepository tables;
    @Autowired private Accountancy accountancy;
    @Autowired private ExpenseGroupRepository expenseGroups;
    @Autowired private BusinessTime businessTime;

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
        Bill bill = new Bill(desk, persons.findByUserAccount(authenticationManager.getCurrentUser().get()).get());
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

    @RequestMapping("/service/bills/details/{billid}/add/{productid}")
    public String addProductToBill(@PathVariable("billid") Long billid, @PathVariable("productid") Long productid) {
        Bill bill = bills.findOne(billid).get();
        DayMenuItem item = dayMenuItems.findOne(productid).get();
        BillItem billItem = new BillItem(item);
        changeBillItem(bill, billItem, 1);
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

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
            List<DayMenuItem> menuItems = dayMenuItems.findAll();
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

    @RequestMapping("/service/bills/details/{billid}/split")
    public String splitBill(@PathVariable("billid") Long billid, @ModelAttribute("first") Optional<String> query, Model model){
        Bill bill = bills.findOne(billid).get();
        if(query.isPresent()) {
            Bill newBill = new Bill(bill.getDesk(), bill.getStaff());
            Map<BillItem, Integer> args = queryToMap(query.get()); //split bill in billItemId,quantity
            for (BillItem billItem : bill.getItems()) { //work with every item that existed before
                if (!args.keySet().contains(billItem)) {
                    bill.changeItem(billItem, 0);
                    newBill.changeItem(billItem, billItem.getQuantity());
                } else {
                    bill.changeItem(billItem, args.get(billItem));
                    newBill.changeItem(new BillItem(billItem.getItem()), billItem.getQuantity() - args.get(billItem));
                }
            }
            bills.save(bill);
            bills.save(newBill);
            return "redirect:/service/bills";

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
                    new Expense(billItem.getItem().getPrice().multiply(diff).multiply(0.9),
                    "Rechnung Nr. " + bill.getId() + ": " + diff + " x " + billItem.getItem().getName(),
                    persons.findByUserAccount(authenticationManager.getCurrentUser().get()).get(),
                    expenseGroups.findByName("Bestellung").get())
            );
        }
        bill.changeItem(billItem, quantity);
    }


}
