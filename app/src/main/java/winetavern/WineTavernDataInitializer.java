package winetavern;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.ChargeLine;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import winetavern.model.DateParameter;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.BillRepository;
import winetavern.model.management.*;
import winetavern.model.stock.Category;
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

import java.time.LocalDateTime;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Initializes Data in the Database for the prototype
 * @author Niklas Wünsche
 */

@Component
public class WineTavernDataInitializer implements DataInitializer{

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private PersonManager personManager;
    @Autowired private EventCatalog eventCatalog;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ProductCatalog products;
    @Autowired private ShiftRepository shifts;
    @Autowired private BillRepository bills;
    private String adminName = "admin";

    @Override
    public void initialize() {
        if(!isAdminInDB(userAccountManager, adminName)) {
            initializeAdmin(userAccountManager);
            initializeEvents();
            initializeStock();
            initializeShift();
            initializeBills();
        }
    }

    private void initializeAdmin(UserAccountManager manager) {
        UserAccount admin = manager.create(adminName, "1234", Role.of("ROLE_ADMIN"));
        admin.setFirstname("Hans-Peter");
        admin.setLastname("Maffay");
        admin.setEmail("peter.maffay@t-online.de");
        manager.save(admin);
        DateParameter date = new DateParameter();
        date.setDay(15);
        date.setMonth(7);
        date.setYear(1979);
        personManager.save(new Person(admin, "Wundstraße 7, 01217 Dresden", date));

    }

    private boolean isAdminInDB(UserAccountManager manager, String adminName) {
        return manager.findByUsername(adminName).isPresent();
    }

    public void initializeEvents() {
        eventCatalog.save(new Event("Go hard or go home - Ü80 Party", Money.of(7, EURO),
                new TimeInterval(LocalDateTime.of(2016, 9, 11, 21, 30), LocalDateTime.of(2016, 9, 11, 23, 30)),
                "SW4G ist ein muss!"));

        eventCatalog.save(new Event("Grillabend mit Musik von Barny dem Barden", Money.of(7, EURO),
                new TimeInterval(LocalDateTime.of(2016, 11, 11, 19, 30), LocalDateTime.of(2016, 11, 11, 23, 30)),
                "Es wird gegrillt und überteuerter Wein verkauft."));
    }

    public void initializeStock() {
        Product vodka = new Product("Vodka", Money.of(12.50, EURO));
        vodka.addCategory(Category.LIQUOR.getCategoryName());

        Product brandstifter = new Product("Berliner Brandstifter", Money.of(33.99, EURO));
        brandstifter.addCategory(Category.LIQUOR.getCategoryName());

        stock.save(new InventoryItem(vodka, Quantity.of(15)));
        stock.save(new InventoryItem(brandstifter, Quantity.of(93)));
    }

    private void initializeBills() {
        Order order = new Order(userAccountManager.findAll().iterator().next(), Cash.CASH);
        order.add(new ChargeLine(Money.of(9.33, EURO), "charge"));
        Bill bill = new Bill(1, order);
        bills.save(bill);
    }

    /**
     * Should be deleted in the final programm
     */
    public void initializeShift() {
        shifts.save(new Shift(new TimeInterval(LocalDateTime.of(2016, 11, 11, 11, 11), LocalDateTime.of(2016, 11, 11, 11, 11).plusHours(3)),
                personManager.findAll().iterator().next()));
    }

}

