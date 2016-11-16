package winetavern.controller;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.DayMenuOrder;
import winetavern.model.menu.DayMenuItem;

/**
 * @author Louis
 */

@Controller
public class BillController {/*
    @Autowired private OrderManager<Order> orders;
    @Autowired private BillRepository bills;
    @Autowired private AuthenticationManager authenticationManager;

    @RequestMapping("/service/bills")
    public String showBills(Model model){
        model.addAttribute("active", orders.findBy(OrderStatus.OPEN));
        model.addAttribute("old", orders.findBy(OrderStatus.COMPLETED));
        return "bills";
    }

    @RequestMapping("/service/bills/add")
    public String addBill(@ModelAttribute String table) {
        Order order = new Order(authenticationManager.getCurrentUser().get(), Cash.CASH);
        Bill bill = new Bill(Integer.parseInt(table), order);
        bills.save(bill);
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}/add/{productid}")
    public String addProductToBill(@PathVariable("billid") Bill bill, @PathVariable("pid") DayMenuItem item,
                                   @ModelAttribute("qantity") String quantity) {
        bill.getOrder().add(new DayMenuOrder(item, Quantity.of(Float.parseFloat(quantity))));
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}")
    public String showBillDetails(@PathVariable("billid") Bill bill) {

        return "bills";
    }*/
}
