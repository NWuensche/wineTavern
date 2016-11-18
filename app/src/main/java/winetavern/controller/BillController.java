package winetavern.controller;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.BillItem;
import winetavern.model.accountancy.BillRepository;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.user.PersonManager;

/**
 * @author Louis
 */

@Controller
public class BillController {
    @Autowired private OrderManager<Order> orders;
    @Autowired private BillRepository bills;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private PersonManager persons;
    @Autowired private DayMenuItemRepository dayMenuItems;

    @RequestMapping("/service/bills")
    public String showBills(Model model){
        model.addAttribute("active", bills.findByIsClosedFalse());
        model.addAttribute("old", bills.findByIsClosedTrue());
        return "bills";
    }

    @RequestMapping("/service/bills/add")
    public String addBill(@ModelAttribute String desk) {
        Bill bill = new Bill(Integer.parseInt(desk), persons.findByUserAccount(authenticationManager.getCurrentUser().get()).get());
        bills.save(bill);
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}/add/{productid}")
    public String addProductToBill(@PathVariable("billid") Bill bill, @PathVariable("pid") DayMenuItem item,
                                   @ModelAttribute("qantity") String quantity) {
        bill.addItem(new BillItem(item, Integer.parseInt(quantity)));
        bills.save(bill);
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}")
    public String showBillDetails(@PathVariable("billid") Bill bill, Model model) {
        model.addAttribute("bill", bill);
        model.addAttribute("menuitems", dayMenuItems.findAll()); //TODO show only items of the day
        return "billdetails";
    }
}
