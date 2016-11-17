package winetavern.controller;

import org.hibernate.mapping.Map;
import org.javamoney.moneta.Money;
import org.salespointframework.order.ChargeLine;
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
import winetavern.model.accountancy.BillRepository;
import winetavern.model.accountancy.DayMenuOrder;
import winetavern.model.menu.DayMenuItem;

import java.util.ArrayList;
import java.util.List;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class BillController {
    @Autowired private OrderManager<Order> orders;
    @Autowired private BillRepository bills;
    @Autowired private AuthenticationManager authenticationManager;

    @RequestMapping("/service/bills")
    public String showBills(Model model){
        List<Bill> active = new ArrayList<>();
        List<Bill> old = new ArrayList<>();
        orders.findBy(OrderStatus.OPEN).forEach(it -> active.add(bills.findByOrder(it).get()));
        orders.findBy(OrderStatus.COMPLETED).forEach(it -> old.add(bills.findByOrder(it).get()));
        model.addAttribute("active", active);
        model.addAttribute("old", old);
        return "bills";
    }

    @RequestMapping("/service/bills/add")
    public String addBill(@ModelAttribute String table) {
        Order order = new Order(authenticationManager.getCurrentUser().get(), Cash.CASH);
        Bill bill = new Bill(1, order);
        bills.save(bill);
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}/add/{productid}")
    public String addProductToBill(@PathVariable("billid") Bill bill, @PathVariable("pid") DayMenuItem item,
                                   @ModelAttribute("qantity") String quantity) {
        bill.getOrder().add(new DayMenuOrder(item, Quantity.of(Float.parseFloat(quantity))));
        bills.save(bill);
        return "bills";
    }

    @RequestMapping("/service/bills/details/{billid}")
    public String showBillDetails(@PathVariable("billid") Bill bill) {

        return "bills";
    }
}
