package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.EmployeeManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * @author Louis
 */

@Controller
public class ShiftController {
    @Autowired private ShiftRepository shifts;
    @Autowired private EmployeeManager employees;
    @Autowired private BusinessTime time;

    @RequestMapping("/admin/management/shifts")
    public String showShifts(Model model) {

        TimeInterval week = getWeekInterval(time.getTime()); //get the week interval out of businessTime
        List<Shift> shiftsOfWeek = getShiftsOfWeek(week);    //get all shifts in this interval

        model.addAttribute("shifts", shiftsOfWeek); //TODO give me the calendar string

        return "shifts";
    }

    @RequestMapping("/admin/management/shifts/change/{shiftid}")
    public String changeShiftData(@PathVariable Long shiftid, Model model) {
        model.addAttribute("shiftdata",shifts.findOne(shiftid).get());
        model.addAttribute("time",getTimes());
        model.addAttribute("employees",employees.findAll());
        return "shifts";
    }

    private TimeInterval getWeekInterval(LocalDateTime date) {
        int day = date.getDayOfWeek().getValue();                                     //returns 1-7
        return new TimeInterval(date.toLocalDate().atStartOfDay().minusDays(day - 1), //last Monday
                date.withHour(23).withMinute(59).withSecond(59).plusDays(7 - day));   //next Sunday
    }

    private List<Shift> getShiftsOfWeek(TimeInterval week) {
        List<Shift> res = new LinkedList<>();
        for (Shift shift : shifts.findAll())
            if (shift.getInterval().intersects(week)) res.add(shift);

        Collections.sort(res, (o1, o2) -> (o1.getInterval().getStart().compareTo(o2.getInterval().getStart())));
        return res;
    }

    private List<String> getTimes(){
        List<String> res = new LinkedList<>();
        for(int i = 0 ; i < 24 * 4 * 15; i = i + 15){
            res.add(checkTime(i / 60) + ":" + checkTime(i % 60));
        }
        return res;
    }

    private String checkTime(int i){
        return i < 10 ? "0" + i : i + "";
    }
}
