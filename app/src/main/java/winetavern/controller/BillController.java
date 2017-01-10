package winetavern.controller;

import lombok.NonNull;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import winetavern.Helper;
import winetavern.model.accountancy.*;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.splitter.SplitBuilder;

import javax.money.MonetaryAmount;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    @NotNull private final DayMenuRepository dayMenus;
    @NotNull private final Inventory<InventoryItem> stock;

    @Autowired
    public BillController(BillRepository bills, DayMenuRepository dayMenus, BillItemRepository billItems, AuthenticationManager authenticationManager, EmployeeManager employees, DayMenuItemRepository dayMenuItems, DeskRepository tables, Accountancy accountancy, ExpenseGroupRepository expenseGroups, BusinessTime businessTime, Inventory<InventoryItem> stock) {
        this.bills = bills;
        this.billItems = billItems;
        this.authenticationManager = authenticationManager;
        this.employees = employees;
        this.dayMenuItems = dayMenuItems;
        this.tables = tables;
        this.accountancy = accountancy;
        this.expenseGroups = expenseGroups;
        this.businessTime = businessTime;
        this.dayMenus = dayMenus;
        this.stock = stock;
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

    private void changeBillItem(Bill bill, BillItem billItem, int quantity) {
        // TODO, was, wenn negativ?
        int diff = quantity - billItem.getQuantity();
        if (diff > 0) { //adds orders to expenses of staff
            addNewExpenseForEmployee(bill, billItem, diff);
        }
        bill.changeItem(billItem, quantity);
    }

    /**
     * the staff pays 90% of the selling price
     */
    private void addNewExpenseForEmployee(Bill bill, BillItem billItem, int difference) {
        MonetaryAmount toPay = billItem.getItem().getPrice().multiply(difference).multiply(0.9).negate();
        String description = "Rechnung Nr. " + bill.getId() + ": " + difference + " x " + billItem.getItem().getName();
        Employee currUser = employees.findByUserAccount(authenticationManager.getCurrentUser().get()).get();
        ExpenseGroup orderGroup = expenseGroups.findByName("Bestellung").get();

        accountancy.add(new Expense(toPay, description, currUser, orderGroup));
    }

    /**
     * @param query Format: billItemID,newNumberOfItems|billItemId,...
     */
    @RequestMapping(value = "/service/bills/details/{billid}",method = RequestMethod.GET)
    public String showBillDetails(@PathVariable("billid") Long billid, @ModelAttribute("save") Optional<String> query, Model model) {
        Bill bill = bills.findOne(billid).get();

        if (query.isPresent() && !query.get().equals("")) {
            Map<BillItem, Integer> args = queryToMap(query.get());
            args.keySet()
                    .stream()
                    .forEach(bItem -> changeBillItem(bill, bItem, args.get(bItem)));

            bills.save(bill);

            return "redirect:/service/bills/details/" + billid;
        }

        model.addAttribute("bill", bill);
        List<DayMenuItem>  menuItems = new LinkedList<>();

        Optional<DayMenu> menuOfDay = dayMenus.findByDay(businessTime.getTime().toLocalDate());
        //menuOfDay.ifPresent(dayMenu -> menuItems.addAll(dayMenu.getDayMenuItems()));

        if (menuOfDay.isPresent()) {
            menuItems = StreamSupport
                    .stream(menuOfDay.get().getDayMenuItems().spliterator(), false)
                    .filter(dItem -> !dayMenuItemsOfBill(bill).contains(dItem))
                    .collect(Collectors.toList());
        }


        model.addAttribute("menuitems", menuItems);
        return "billdetails";

    }

    private Set<DayMenuItem> dayMenuItemsOfBill(Bill bill) {
        return bill
                .getItems()
                .stream()
                .map(BillItem::getItem)
                .collect(Collectors.toSet());
    }

    @RequestMapping("/service/bills/details/{billid}/print")
    public String printBill(@PathVariable("billid") Long billid, Model model){
        Bill bill = bills.findOne(billid).get();
        if (!bill.isClosed()) {
            bill.close(businessTime);
            bills.save(bill);
            removeBillItemsFromStock(bill);
        }
        model.addAttribute("time",Helper.localDateTimeToDateTimeString(businessTime.getTime()));
        model.addAttribute("bill", bill);
        return "printbill";
    }

    private void removeBillItemsFromStock(Bill bill) {
        for (BillItem billItem : bill.getItems()) {
            InventoryItem inventoryItem = stock.findByProduct(billItem.getItem().getProduct()).get();
            try {
                inventoryItem.decreaseQuantity(
                        Quantity.of(1 / billItem.getItem().getQuantityPerProduct())
                );
            } catch (Exception e) {
                inventoryItem.decreaseQuantity(
                    inventoryItem.getQuantity()
                );
            }
            stock.save(inventoryItem);
        }
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
        String[] args = splitQuery(query);

        return getResources(args);
    }

    private String[] splitQuery(String query) {
        return query.substring(0, query.length() - 1).split("\\|");
    }

    /**
     * split bill into \<BillItem, quantity\> Map
     * @return
     */
    private Map<BillItem, Integer> getResources(String[] args) {
        Map<BillItem, Integer> res = new HashMap<>();

        for (String arg : args) {
            Long billItemId = Long.parseLong(arg.split(",")[0]);
            Integer quantityOfItem = Integer.parseInt(arg.split(",")[1]);
            res.put(billItems.findOne(billItemId).get(), quantityOfItem);
        }

        return res;
    }

}
