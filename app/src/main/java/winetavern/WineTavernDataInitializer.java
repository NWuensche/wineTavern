package winetavern;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import winetavern.model.accountancy.ExpenseGroup;
import winetavern.model.accountancy.ExpenseGroupRepository;
import winetavern.model.management.*;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.reservation.Desk;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.stock.Category;
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;
import winetavern.model.user.PersonTitle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Initializes Data
 * @author Niklas Wünsche
 */

@Component
public class WineTavernDataInitializer implements DataInitializer{

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private PersonManager personManager;
    @Autowired private EventCatalog eventCatalog;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ExpenseGroupRepository expenseGroups;
    @Autowired private ShiftRepository shifts;
    @Autowired private DeskRepository deskRepository;
    @Autowired private DayMenuRepository dayMenuRepository;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private ProductCatalog productCatalog;

    private String adminName = "admin";

    @Override
    public void initialize() {
        if(!isAdminInDB(userAccountManager, adminName)) {
            initializeAdmin(userAccountManager);
            initializeEvents();
            initializeStock();
            initializeShift();
            initializeExpenseGroups();
            initializeTables();
            initializeDayMenuWithItems();
        }
    }

    private void initializeAdmin(UserAccountManager manager) {
        UserAccount admin = manager.create(adminName, "1234", Role.of("ROLE_ADMIN"));
        admin.setFirstname("Hans-Peter");
        admin.setLastname("Maffay");
        admin.setEmail("peter.maffay@t-online.de");
        manager.save(admin);
        String birthday = "1979/07/15";
        personManager.save(new Person(admin, "Wundtstraße 7, 01217 Dresden", birthday, PersonTitle.MISTER.getGerman()));
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
        vodka.addCategory(Category.LIQUOR.toString());

        Product brandstifter = new Product("Berliner Brandstifter", Money.of(33.99, EURO));
        brandstifter.addCategory(Category.LIQUOR.toString());

        stock.save(new InventoryItem(vodka, Quantity.of(15)));
        stock.save(new InventoryItem(brandstifter, Quantity.of(93)));
    }

    private void initializeExpenseGroups() {
        expenseGroups.save(new ExpenseGroup("Bestellung"));
        expenseGroups.save(new ExpenseGroup("Künstlergage"));
    }

    public void initializeTables() {
        //Ordinary desks
        List<Desk> deskList = new ArrayList<Desk>();
        deskList.add(new Desk("1", 8));
        deskList.add(new Desk("2", 8));
        deskList.add(new Desk("3", 2));
        deskList.add(new Desk("4", 2));
        deskList.add(new Desk("5", 2));
        deskList.add(new Desk("6", 2));
        deskList.add(new Desk("7", 2));


        //Bar
        for(int i = 1; i <= 7; i++)
            deskList.add(new Desk("B" + String.valueOf(i), 1));

        deskRepository.save(deskList);
    }

    public void initializeDayMenuWithItems() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, 10, 30);
        DayMenu dayMenu = new DayMenu(calendar);
        dayMenuRepository.save(dayMenu);

        DayMenuItem vodka = new DayMenuItem("Vodka 2cl vom Fass", "really good", Money.of(2, "EUR"), 35.0);
        vodka.setProduct(productCatalog.findByName("Vodka").iterator().next());
        vodka.addDayMenu(dayMenu);
        vodka.enable();
        dayMenuItemRepository.save(vodka);

        DayMenuItem vodka2cl = new DayMenuItem("Vodka 2cl", "Kleiner Vodka für zwischendurch", Money.of(1.80, "EUR"), 35.0);
        vodka2cl.setProduct(productCatalog.findByName("Vodka").iterator().next());
        vodka2cl.addDayMenu(dayMenu);
        vodka2cl.enable();
        dayMenuItemRepository.save(vodka2cl);

        DayMenuItem vodka4cl = new DayMenuItem("Vodka 4cl", "Großer Vodka für coole Leute", Money.of(2.50, "EUR"), 12.0);
        vodka4cl.setProduct(productCatalog.findByName("Vodka").iterator().next());
        vodka4cl.addDayMenu(dayMenu);
        vodka4cl.enable();
        dayMenuItemRepository.save(vodka4cl);

        DayMenuItem berlinerBrandstifter = new DayMenuItem("Berliner Brandstifter", "der beste", Money.of(1.99, "EUR"), 12.0);
        berlinerBrandstifter.setProduct(productCatalog.findByName("Berliner Brandstifter").iterator().next());
        berlinerBrandstifter.addDayMenu(dayMenu);
        dayMenuItemRepository.save(berlinerBrandstifter);


    }

    /**
     * Should be deleted in the final programm
     */
    public void initializeShift() {
        shifts.save(new Shift(new TimeInterval(LocalDateTime.of(2016, 11, 11, 11, 11), LocalDateTime.of(2016, 11, 11, 11, 11).plusHours(3)),
                personManager.findAll().iterator().next()));
    }

}

