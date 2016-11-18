package winetavern.controller;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.BillItem;
import winetavern.model.accountancy.BillItemRepository;
import winetavern.model.accountancy.BillRepository;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.user.PersonManager;

import java.util.List;

/**
 * @author Louis
 */

@Controller
public class BillController {
    @Autowired private OrderManager<Order> orders;
    @Autowired private BillRepository bills;
    @Autowired private BillItemRepository billItems;
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
    public String addProductToBill(@PathVariable("billid") Long billid, @PathVariable("productid") Long productid) {
        System.out.println(billid + " : " + productid);
        Bill bill = bills.findOne(billid).get();
        DayMenuItem item = dayMenuItems.findOne(productid).get();
        BillItem billItem = new BillItem(item, 1);
        bill.addItem(billItem);
        //billItems.save(billItem);
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

    @RequestMapping("/service/bills/details/{billid}")
    public String showBillDetails(@PathVariable("billid") Long billid, Model model) {
        Bill bill = bills.findOne(billid).get();
        model.addAttribute("bill", bill);
        List<DayMenuItem> menuItems = dayMenuItems.findAll();
        bill.getItems().forEach(it -> menuItems.remove(it.getItem()));
        model.addAttribute("menuitems", menuItems); //TODO show
        // only items
        // of the day
        return "billdetails";
    }
}
