package winetavern;

import org.javamoney.moneta.Money;
import org.salespointframework.accountancy.Accountancy;
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
import winetavern.model.accountancy.Expense;
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
import winetavern.model.user.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Initializes Data
 * @author Niklas Wünsche
 */

@Component
public class WineTavernDataInitializer implements DataInitializer{
    @Autowired private UserAccountManager userAccountManager;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private EventCatalog eventCatalog;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private Accountancy accountancy;
    @Autowired private ExpenseGroupRepository expenseGroups;
    @Autowired private ShiftRepository shifts;
    @Autowired private DeskRepository deskRepository;
    @Autowired private DayMenuRepository dayMenuRepository;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private ProductCatalog productCatalog;
    @Autowired private ExternalManager externalManager;
    @Autowired private VintnerManager vintnerManager;

    private final String adminName = "admin";
    private final boolean initializeSamples = true; //set to true to create samples for all entities

    private final String nameRedWine = "Riesling trocken";
    private final String nameWhiteWine = "Chateau Providence";
    private final String nameLiquor = "Williams Christ Birnenbrand";
    private final String nameSoftDrink = "Coca Cola";
    private final String nameSnack = "Salzstangen";
    private final String nameMenu = "Käseplatte";

    @Override
    public void initialize() {
        if(!isAdminInDB(adminName)) {
            initializeAdmin();
            initializeExpenseGroups();
            initializeTables();
        }

        if (initializeSamples && !isServiceInDB()) {
            initializeService();
            initializeEvents();
            initializeStockWithVintners();
            initializeShift();
            initializeDayMenuWithItems();
            initializeExternals();
        }
    }

    private void initializeAdmin() {
        UserAccount admin = userAccountManager.create(adminName, "1234", Role.of("ROLE_ADMIN"));
        admin.setFirstname("Hans-Peter");
        admin.setLastname("Roch");
        admin.setEmail("peter.roch@t-online.de");
        userAccountManager.save(admin);
        String birthday = "1979/07/15";
        employeeManager.save(new Employee(admin, "Wundtstraße 7, 01217 Dresden", birthday, PersonTitle.MISTER.getGerman()));
    }

    private void initializeService() {
        String birthday;
        UserAccount service = userAccountManager.create("weber", "1234", Role.of("ROLE_SERVICE"));
        service.setFirstname("Sabine");
        service.setLastname("Weber");
        service.setEmail("sabine.weber@coolmail.com");
        userAccountManager.save(service);
        birthday = "1998/05/12";
        employeeManager.save(new Employee(service, "Engelweg 5, 66666 Downtown", birthday, PersonTitle.MISSES.getGerman()));

        UserAccount cook = userAccountManager.create("gauck", "1234", Role.of("ROLE_COOK"));
        cook.setFirstname("Martin");
        cook.setLastname("Gauck");
        cook.setEmail("martin.gauck@mymail.com");
        userAccountManager.save(cook);
        birthday = "1966/09/14";
        employeeManager.save(new Employee(cook, "Sägeweg 51, 12425 Michelshausen", birthday, PersonTitle.MISTER.getGerman()));

        UserAccount accountant = userAccountManager.create("ostertag", "1234", Role.of("ROLE_ACCOUNTANT"));
        accountant.setFirstname("Alexandra");
        accountant.setLastname("Ostertag");
        accountant.setEmail("a.ostertag@web.com");
        userAccountManager.save(accountant);
        birthday = "1983/05/01";
        employeeManager.save(new Employee(accountant, "Fichtengasse 9, 07485 Dettau", birthday, PersonTitle.MISSES.getGerman()));
    }

    private boolean isAdminInDB(String adminName) {
        return userAccountManager.findByUsername(adminName).isPresent();
    }

    private boolean isServiceInDB() {
        return userAccountManager.findByUsername("weber").isPresent();
    }

    private void initializeEvents() {
        eventCatalog.save(new Event("Buchvorstellung: Harry Potter - The Cursed Child", Money.of(7, EURO),
                new TimeInterval(LocalDate.now().atTime(19, 30), LocalDate.now().atTime(21, 0)),
                "Eine liebevolle Vorschau des neuen Harry Potter Buches. Dazu gibt es köstlichen Wein und Kaminfeuer.",
                new External("Maria Sanfler", Money.of(180, EURO))));

        eventCatalog.save(new Event("Poetry Slam", Money.of(7, EURO),
                new TimeInterval(LocalDate.now().atTime(16, 0), LocalDate.now().atTime(19, 0)),
                "Moderne Poesie gibt es nicht? - Falsch! Studenten der TU Dresden zeigen ihr Können.",
                new External("Poetry Slam CLub Dresden", Money.of(50, EURO))));
        // TODO Why does this crash the DB in the tests?
//        Expense exp1 = new Expense(Money.of(180,"EUR"),
//                "Maria Sanfler Buchvorstellung",
//                externalManager.findByName("Maria Sanfler").get(),
//                expenseGroups.findByName("Künstlergage").get());
//        exp1.cover();
//        accountancy.add(exp1);
    }

    private void initializeStockWithVintners() {
        Product white = new Product(nameWhiteWine, Money.of(10, EURO));
        white.addCategory(Category.WHITE_WINE+"");

        Product red = new Product(nameRedWine, Money.of(35, EURO));
        red.addCategory(Category.RED_WINE+"");

        Product liquor = new Product(nameLiquor, Money.of(14.5, EURO));
        liquor.addCategory(Category.LIQUOR+"");

        Product cola = new Product(nameSoftDrink, Money.of(1.5, EURO));
        cola.addCategory(Category.SOFT_DRINK+"");

        Product snack = new Product(nameSnack, Money.of(3, EURO));
        snack.addCategory(Category.SNACK+"");

        Product menu = new Product(nameMenu, Money.of(10, EURO));
        menu.addCategory(Category.MENU+"");

        stock.save(new InventoryItem(white, Quantity.of(13)));
        stock.save(new InventoryItem(red, Quantity.of(24)));
        stock.save(new InventoryItem(liquor, Quantity.of(8)));
        stock.save(new InventoryItem(cola, Quantity.of(93)));
        stock.save(new InventoryItem(snack, Quantity.of(28)));
        stock.save(new InventoryItem(menu, Quantity.of(17)));

        Vintner vintner1 = new Vintner("Remstalkellerei Weinstadt", 1);
        Vintner vintner2 = new Vintner("Daniels Weine", 2);
        Vintner vintner3 = new Vintner("Traubenwunder Berlin", 3);

        vintner1.addWine(red);
        vintner2.addWine(white);

        vintnerManager.save(vintner1);
        vintnerManager.save(vintner2);
        vintnerManager.save(vintner3);
    }

    private void initializeExpenseGroups() {
        expenseGroups.save(new ExpenseGroup("Bestellung"));
        expenseGroups.save(new ExpenseGroup("Künstlergage"));
        expenseGroups.save(new ExpenseGroup("Abrechnung"));
    }

    private void initializeTables() {
        //Ordinary desks
        List<Desk> deskList = new ArrayList<Desk>();
        deskList.add(new Desk("1", 8));
        deskList.add(new Desk("2", 8));
        deskList.add(new Desk("3", 2));
        deskList.add(new Desk("4", 3));
        deskList.add(new Desk("5", 2));
        deskList.add(new Desk("6", 4));
        deskList.add(new Desk("7", 4));
        deskList.add(new Desk("8", 3));
        deskList.add(new Desk("9", 4));
        deskList.add(new Desk("Bar", 7));

        deskRepository.save(deskList);
    }

    private void initializeDayMenuWithItems() {
        DayMenu dayMenu = new DayMenu(LocalDate.now());
        dayMenuRepository.save(dayMenu);

        DayMenuItem smallWhiteWine = new DayMenuItem(nameWhiteWine + " 0,2l", nameWhiteWine + ": Glas",
                Money.of(4.75, EURO), 0.75/0.2);
        smallWhiteWine.setProduct(productCatalog.findByName(nameWhiteWine).iterator().next());
        dayMenu.addMenuItem(smallWhiteWine);
        //smallWhiteWine.addDayMenu(dayMenu);
        dayMenuItemRepository.save(smallWhiteWine);

        DayMenuItem bigWhiteWine = new DayMenuItem(nameWhiteWine + " 0.75l", nameWhiteWine + ": Flasche",
                Money.of(17, EURO), 1.0);
        bigWhiteWine.setProduct(productCatalog.findByName(nameWhiteWine).iterator().next());
        dayMenu.addMenuItem(bigWhiteWine);
        dayMenuItemRepository.save(bigWhiteWine);

        DayMenuItem smallRedWine = new DayMenuItem(nameRedWine + " 0,2l", nameRedWine + ": Glas",
                Money.of(12.99, EURO), 0.75/0.2);
        smallRedWine.setProduct(productCatalog.findByName(nameRedWine).iterator().next());
        dayMenu.addMenuItem(smallRedWine);
        dayMenuItemRepository.save(smallRedWine);

        DayMenuItem bigRedWine = new DayMenuItem(nameRedWine + " 0,75l", nameRedWine + ": Flasche",
                Money.of(45, EURO), 1.0);
        bigRedWine.setProduct(productCatalog.findByName(nameRedWine).iterator().next());
        dayMenu.addMenuItem(bigRedWine);
        dayMenuItemRepository.save(bigRedWine);

        DayMenuItem smallLiquor = new DayMenuItem(nameLiquor + " 2cl", nameLiquor + ": Kurzer",
                Money.of(2.99, EURO), 0.75/0.02);
        smallLiquor.setProduct(productCatalog.findByName(nameLiquor).iterator().next());
        dayMenu.addMenuItem(smallLiquor);
        dayMenuItemRepository.save(smallLiquor);

        DayMenuItem bigLiquor = new DayMenuItem(nameLiquor + " 4cl", nameLiquor + ": Doppelter",
                Money.of(4.99, EURO), 0.75/0.04);
        bigLiquor.setProduct(productCatalog.findByName(nameLiquor).iterator().next());
        dayMenu.addMenuItem(bigLiquor);
        dayMenuItemRepository.save(bigLiquor);

        DayMenuItem smallSoftDrink = new DayMenuItem(nameSoftDrink + " 0,2l", nameSoftDrink + ": klein",
                Money.of(2.99, EURO), 1/0.2);
        smallSoftDrink.setProduct(productCatalog.findByName(nameSoftDrink).iterator().next());
        dayMenu.addMenuItem(smallSoftDrink);
        dayMenuItemRepository.save(smallSoftDrink);

        DayMenuItem mediumSoftDrink = new DayMenuItem(nameSoftDrink + " 0,4l", nameSoftDrink + ": mittel",
                Money.of(4.29, EURO), 1/0.4);
        mediumSoftDrink.setProduct(productCatalog.findByName(nameSoftDrink).iterator().next());
        dayMenu.addMenuItem(mediumSoftDrink);
        dayMenuItemRepository.save(mediumSoftDrink);

        DayMenuItem bigSoftDrink = new DayMenuItem(nameSoftDrink + " 0,8l", nameSoftDrink + ": groß",
                Money.of(5.99, EURO), 1/0.8);
        bigSoftDrink.setProduct(productCatalog.findByName(nameSoftDrink).iterator().next());
        dayMenu.addMenuItem(mediumSoftDrink);
        dayMenuItemRepository.save(bigSoftDrink);

        DayMenuItem snack = new DayMenuItem(nameSnack, nameSnack,
                Money.of(1.99, EURO), 8.0);
        snack.setProduct(productCatalog.findByName(nameSnack).iterator().next());
        dayMenu.addMenuItem(snack);
        dayMenuItemRepository.save(snack);

        DayMenuItem menu = new DayMenuItem(nameMenu, nameMenu,
                Money.of(5.99, EURO), 1.0);
        menu.setProduct(productCatalog.findByName(nameMenu).iterator().next());
        dayMenu.addMenuItem(menu);
        dayMenuItemRepository.save(menu);
    }

    private void initializeShift() {
        Random random = new Random();
        LocalDate start = LocalDate.now().with(DayOfWeek.TUESDAY);
        LocalTime openingTime = LocalTime.of(10, 0);
        LocalTime closingTime = LocalTime.of(23, 0);
        for (int i = 0; i < 6; i++) {
            for (Employee employee : employeeManager.findAll()) {
                Shift shift = new Shift(new TimeInterval(start.plusDays(i).atTime(openingTime).plusMinutes(15 * random.nextInt(16)),
                        start.plusDays(i).atTime(closingTime.minusMinutes(15 * random.nextInt(16)))), employee);
                shifts.save(shift);
            }
        }
    }

    private void initializeExternals() {
        externalManager.save(new External("Posaunenchor Dresden", Money.of(300, EURO)));
    }

}

