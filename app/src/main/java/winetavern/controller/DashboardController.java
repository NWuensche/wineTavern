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

import javax.money.MonetaryAmount;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

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

        if (authenticationManager.getCurrentUser().get().hasRole(Role.of("ROLE_ADMIN"))) {
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

            // TODO, was wenn es nicht existert?
            Optional<DayMenu> menu = daymenus.findByDay(time.getTime().toLocalDate());
            menu.ifPresent(m ->  model.addAttribute("menu", m));

            model.addAttribute("orders",billItemRepository.findByReadyFalse());

            return "startcook";
        } else {
            return "backend-temp";
        }
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

        String res = "";
        List<Reservation> reservations = new ArrayList<>();
        List<Reservation> reservationsToday = new ArrayList<>();
        reservationRepository.findAllByOrderByDesk().forEach(it -> reservations.add(it));
        for(Reservation reservation : reservations){
            if(today.timeInInterval(reservation.getReservationStart())){
                reservationsToday.add(reservation);
            }
        }

        for(Reservation reservation : reservationsToday){
            res += "\"table\":\"" + reservation.getDesk().getName() +
                    "\",\"person\":\"" + reservation.getGuestName() +
                    "\",\"start\":\"" + Helper.localDateTimeToJavascriptDateString(reservation.getInterval().getStart()) +
                    "\",\"end\":\"" + Helper.localDateTimeToJavascriptDateString(reservation.getInterval().getEnd()) +
                    "\"|";
        }

        return res;
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

        for (AccountancyEntry entry : accountancy.findAll()) {
            Expense expense  = (Expense) entry;
            LocalDate date = expense.getDate().get().toLocalDate();
            if (expense.isCovered() && groups.contains(expense.getExpenseGroup()) && incomeMap.containsKey(date))
                if(expense.getValue().isLessThan(Money.of(0,"EUR")))
                    incomeMap.put(date, incomeMap.get(date).add(expense.getValue()));
                else
                    lossMap.put(date, lossMap.get(date).add(expense.getValue()));
        }


        String res = "[[\"Tag\",\"Einnahmen\",\"Ausgaben\",\"Schnitt\"],";

        for(Map.Entry<LocalDate, MonetaryAmount> entry : incomeMap.entrySet()){
            res += "[\"" + entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.GERMAN) +
                   "\"," + entry.getValue().negate().getNumber().doubleValue() +
                   "," + lossMap.get(entry.getKey()).negate().getNumber().doubleValue() +
                    "," + entry.getValue().add(lossMap.get(entry.getKey())).negate().getNumber().doubleValue() +
                    "],";
        }

        res = res.substring(0,res.length()-1) + "]";

        return res;
    }

    private String personalIncomeString(Employee employee){
        Map<LocalDate, MonetaryAmount> incomeMap = new TreeMap<>();
        Map<LocalDate, MonetaryAmount> lossMap = new TreeMap<>();

        for (int i = 7; i >= 0; i--) {
            incomeMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
            lossMap.put(time.getTime().toLocalDate().minusDays(i), Money.of(0, "EUR"));
        }

        for(Bill bill : bills.findByIsClosedTrue()){
            LocalDate date = bill.getCloseTime().toLocalDate();
            if(bill.getStaff().equals(employee) && incomeMap.containsKey(date))
                incomeMap.put(date,incomeMap.get(date).add(bill.getPrice()));
        }

        for (AccountancyEntry entry : accountancy.findAll()) {
            Expense expense  = (Expense) entry;
            LocalDate date = expense.getDate().get().toLocalDate();
            if((expense.getPerson() == employee) &&
                    (expense.isCovered()) &&
                    (expense.getExpenseGroup().getName().equals("Abrechnung")))
                lossMap.put(date,lossMap.get(date).add(expense.getValue()));
        }


        String res = "[[\"Tag\",\"Ausgaben\",\"Gewinn\"],";

        for(Map.Entry<LocalDate, MonetaryAmount> entry : lossMap.entrySet()){
            res += "[\"" + entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.GERMAN) +
                    "\"," + entry.getValue().negate().getNumber().doubleValue() +
                    "," + incomeMap.get(entry.getKey()).add(entry.getValue()).getNumber().doubleValue() +
                    "],";
        }

        res = res.substring(0,res.length()-1) + "]";

        return res;
    }

    private Set<String> getNews(){
        Set<String> res = new HashSet<>();

        for(Event event : events.findAll()){
            if(event.getInterval().getStart().toLocalDate().equals(time.getTime().toLocalDate())){
                res.add("<b>" + event.getName() + "</b> <i>" +
                        Helper.localDateTimeToTimeString(event.getInterval().getStart()) + " - " +
                        Helper.localDateTimeToTimeString(event.getInterval().getEnd()) + "</i><br/>" +
                        event.getDescription() + "<hr/>");
            }
        }



        return res;
    }

}
