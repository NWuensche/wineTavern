package winetavern.controller;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.BillItem;
import winetavern.model.accountancy.BillItemRepository;
import winetavern.model.accountancy.BillRepository;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.user.PersonManager;

import java.util.List;
import java.util.Optional;

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
    @Autowired private DeskRepository tables;

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
        BillItem billItem = new BillItem(item, 1);
        bill.addItem(billItem);
        bills.save(bill);
        return "redirect:/service/bills/details/" + bill.getId();
    }

    @RequestMapping("/service/bills/details/{billid}/remove/{productid}")
    public String removeProductFromBill(@PathVariable("billid") Long billid, @PathVariable("productid") Long
            productid) {

        //TODO needs to be removed from bill, if quantity is just 1
        System.out.println("remove item " + productid);

        return "redirect:/service/bills/details/" + billid;
    }

    @RequestMapping(value = "/service/bills/details/{billid}",method = RequestMethod.GET)
    public String showBillDetails(@PathVariable("billid") Long billid, @ModelAttribute("save") Optional<String> query, Model model) {
        System.out.println("id: " + billid);
        Bill bill = bills.findOne(billid).get();
        if (query.isPresent() && !query.equals("")) {
            //System.out.println(query.get().substring(0, query.get().length() - 1));
            String[] args = query.get().substring(0, query.get().length() - 1).split("\\|"); //split bill in arguments
            for (String arg : args) { //split bill in billItemId,quantity
                //System.out.println("arg: " + arg);
                String[] itemString = arg.split(",");
                //System.out.println("add: " + itemString[0] + "," + itemString[1]);
                bill.changeItem(billItems.findOne(Long.parseLong(itemString[0])).get(), Integer.parseInt(itemString[1]));
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
    public String printBill(@PathVariable("billid") Long billid,Model model){
        //TODO close Bill + we need to talk how to print bill? via HTML or Java?
        model.addAttribute("bill",bills.findOne(billid).get());
        return "printbill";
    }
}
