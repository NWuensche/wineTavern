package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Louis
 */

@Controller
public class ShiftController {
    private final ShiftRepository shifts;
    private final BusinessTime time;
    @Autowired private PersonManager personManager;

    @Autowired
    public ShiftController(ShiftRepository shifts, BusinessTime time) {
        this.shifts = shifts;
        this.time = time;
    }

    @RequestMapping("/admin/management/shifts")
    public String showShifts(Model model) {

        TimeInterval week = getWeekInterval(time.getTime()); //get the week interval out of businessTime
        Set<Shift> shiftsOfWeek = getShiftsOfWeek(week);     //get all shifts in this interval

        model.addAttribute("weekStart", week.getStart().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        model.addAttribute("weekEnd", week.getEnd().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        model.addAttribute("shiftAmount", shiftsOfWeek.size());
        model.addAttribute("shifts", shifts.findAll());

        return "shifts";
    }

    private TimeInterval getWeekInterval(LocalDateTime date) {
        int day = date.getDayOfWeek().getValue();                                                  //returns 1-7
        TimeInterval week = new TimeInterval(date.toLocalDate().atStartOfDay().minusDays(day - 1), //last Monday
                date.withHour(23).withMinute(59).withSecond(59).plusDays(7 - day));                //next Sunday
        return week;
    }

    private Set<Shift> getShiftsOfWeek(TimeInterval week) {

        Set<Shift> res = new TreeSet<>();

        for (Shift shift : shifts.findAll()) {
            TimeInterval shiftTime = shift.getInterval();
            if (shiftTime.getStart().compareTo(week.getStart()) == 1 || //start of shift in week
                    shiftTime.getEnd().compareTo(week.getEnd()) == -1) //or end of shift in week
                res.add(shift);
        }

        return res;
    }
}
