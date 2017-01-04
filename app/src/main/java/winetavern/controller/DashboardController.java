package winetavern.controller;

import lombok.NonNull;
import org.javamoney.moneta.Money;
import org.omg.CORBA.portable.Streamable;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.Helper;
import winetavern.model.accountancy.*;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;
import winetavern.splitter.SplitBuilder;

import javax.money.MonetaryAmount;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sev
 */

@Controller
public class DashboardController {
    @NonNull @Autowired BusinessTime time;
    @NonNull @Autowired AuthenticationManager authenticationManager;
    @NonNull @Autowired EmployeeManager employeeManager;
    @NonNull @Autowired ShiftRepository shifts;
    @NonNull @Autowired ShiftController shiftController;
    @NonNull @Autowired DayMenuRepository daymenus;
    @NonNull @Autowired ReservationRepository reservationRepository;
    @NonNull @Autowired Accountancy accountancy;
    @NonNull @Autowired ExpenseGroupRepository expenseGroups;
    @NonNull @Autowired BillRepository bills;
    @NonNull @Autowired BillItemRepository billItemRepository;
    @NonNull @Autowired EventCatalog events;

    @RequestMapping("/dashboard")
    public String showDashboard(Model model) {
        if (authenticationManager.getCurrentUser().get().hasRole(Role.of("ROLE_ADMIN")) || authenticationManager
                .getCurrentUser().get().hasRole(Role.of("ROLE_ACCOUNTANT"))) {
            model.addAttribute("time", time);

            model.addAttribute("shifts", shiftController.getShiftsOfDay(time.getTime().toLocalDate()));

            Optional<DayMenu> menu = daymenus.findByDay(time.getTime().toLocalDate());
            menu.ifPresent(m ->  model.addAttribute("daymenu", m));
               ;

            model.addAttribute("reservations", reservationString());
            model.addAttribute("income", incomeString());

            return "startadmin";
        } else if (authenticationManager.getCurrentUser().get().hasRole(Role.of("ROLE_SERVICE"))) {
            model.addAttribute("time", time);

            model.addAttribute("news", getNews());

            model.addAttribute("shifts", shiftController.getShiftsOfDay(time.getTime().toLocalDate()));

            Optional<DayMenu> menu = daymenus.findByDay(time.getTime().toLocalDate());
            menu.ifPresent(m ->  model.addAttribute("daymenu", m));

            model.addAttribute("reservations", reservationString());
            model.addAttribute("income", personalIncomeString(
                    employeeManager.findByUserAccount(
                            authenticationManager.getCurrentUser().get())
                            .get()));

            return "startservice";

        } else if (authenticationManager.getCurrentUser().get().hasRole(Role.of("ROLE_COOK"))){
            model.addAttribute("time", time);

            Optional<DayMenu> menu = daymenus.findByDay(time.getTime().toLocalDate());
            menu.ifPresent(m ->  model.addAttribute("menu", m));

            model.addAttribute("orders",billItemRepository.findByReadyFalse());

            return "startcook";
        }

        return "backend-temp";
    }

    // TODO Should this be here?
    @RequestMapping("/cook/ready/{billItemId}")
    public String ready(@PathVariable Long billItemId){
        BillItem item = billItemRepository.findOne(billItemId).get();
        item.ready();
        billItemRepository.save(item);

        return "redirect:/dashboard";
    }


    private String reservationString(){
        TimeInterval today = new TimeInterval(time.getTime().toLocalDate().atStartOfDay().plusNanos(1),
                time.getTime().toLocalDate().atTime(23,59,59));

        Predicate<Reservation> isToday = reservation -> today.timeInInterval(reservation.getReservationStart());

        return reservationRepository.findAllByOrderByDesk()
                .stream()
                .filter(isToday)
                .map(reservation -> "\"table\":\"" + reservation.getDesk().getName() +
                            "\",\"person\":\"" + reservation.getGuestName() +
                            "\",\"start\":\"" + Helper.localDateTimeToJavascriptDateString(reservation.getInterval().getStart()) +
                            "\",\"end\":\"" + Helper.localDateTimeToJavascriptDateString(reservation.getInterval().getEnd()) +
                            "\"|"
                )
                .reduce("", (acc, res) -> acc.concat(res));
    }

    private String incomeString(){
        Map<LocalDate, MonetaryAmount> incomeMap = new TreeMap<>();
        Map<LocalDate, MonetaryAmount> lossMap = new TreeMap<>();

        List<ExpenseGroup> groups = new ArrayList<>();
        expenseGroups.findAll().forEach(it -> groups.add(it));
        groups.remove(expenseGroups.findByName("Bestellung").get());

        for (int i = 7; i >= 0; i--) {
            incomeMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
            lossMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
        }

        Stream<Expense> expenseStream = accountancy.findAll()
                .stream()
                .map(entry -> (Expense) entry)
                .filter(Expense::isCovered)
                .filter(expense -> groups.contains(expense.getExpenseGroup()))
                .filter(expense -> incomeMap.containsKey(expense.getDate().get().toLocalDate()));

        SplitBuilder.splitCollection(expenseStream.collect(Collectors.toList()))
                .splitBy(expense -> expense.getValue().isLessThan(Money.of(0,"EUR")))
                .forEachPassed(expense -> {
                    LocalDate date = expense.getDate().get().toLocalDate();
                    incomeMap.put(date, incomeMap.get(date).add(expense.getValue()));
                })
                .forEachNotPassed(expense -> {
                    LocalDate date = expense.getDate().get().toLocalDate();
                    lossMap.put(date, lossMap.get(date).add(expense.getValue()));
                });

        String res = incomeMap.entrySet()
                .stream()
                .map(entry -> "[\"" + entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.GERMAN) +
                        "\"," + entry.getValue().negate().getNumber().doubleValue() +
                        "," + lossMap.get(entry.getKey()).negate().getNumber().doubleValue() +
                        "," + entry.getValue().add(lossMap.get(entry.getKey())).negate().getNumber().doubleValue() +
                        "],")
                .reduce("[[\"Tag\",\"Einnahmen\",\"Ausgaben\",\"Schnitt\"],", (acc, entry) -> acc.concat(entry));

        res = res.substring(0,res.length()-1) + "]"; // TODO Isn't this res = res.concat("]");?

        return res;
    }

    private String personalIncomeString(Employee employee){
        Map<LocalDate, MonetaryAmount> incomeMap = new TreeMap<>();
        Map<LocalDate, MonetaryAmount> lossMap = new TreeMap<>();

        for (int i = 7; i >= 0; i--) {
            incomeMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
            lossMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
        }

        bills.findByIsClosedTrue()
                .stream()
                .filter(bill -> bill.getStaff().equals(employee))
                .filter(bill -> incomeMap.containsKey(bill.getCloseTime().toLocalDate()))
                .forEach(bill -> {
                    LocalDate date = bill.getCloseTime().toLocalDate();
                    incomeMap.put(date, incomeMap.get(date).add(bill.getPrice()));
                });

        accountancy.findAll()
                .stream()
                .map(entry -> (Expense) entry)
                .filter(expense -> expense.getPerson().equals(employee))
                .filter(expense -> expense.getExpenseGroup().getName().equals("Abrechnung"))
                .forEach(expense -> {
                    LocalDate date = expense.getDate().get().toLocalDate();
                    lossMap.put(date,lossMap.get(date).add(expense.getValue()));
                });


        String res = lossMap.entrySet()
                .stream()
                .map(entry -> "[\"" + entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.GERMAN) +
                        "\"," + entry.getValue().negate().getNumber().doubleValue() +
                        "," + incomeMap.get(entry.getKey()).add(entry.getValue()).getNumber().doubleValue() +
                        "],")
                .reduce("[[\"Tag\",\"Ausgaben\",\"Gewinn\"],", (acc, entry) -> acc.concat(entry));

        res = res.substring(0,res.length()-1) + "]"; // TODO Isn't this res = res.concat("]");?

        return res;
    }

    private Set<String> getNews(){
        return events
                .stream()
                .filter(event -> event.getInterval().getStart().toLocalDate().equals(time.getTime().toLocalDate()))
                .map(event -> "<b>" + event.getName() + "</b> <i>" +
                        Helper.localDateTimeToTimeString(event.getInterval().getStart()) + " - " +
                        Helper.localDateTimeToTimeString(event.getInterval().getEnd()) + "</i><br/>" +
                        event.getDescription() + "<hr/>")
                .collect(Collectors.toSet());
    }

}
