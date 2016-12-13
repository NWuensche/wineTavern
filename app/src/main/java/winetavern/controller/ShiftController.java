package winetavern.controller;

import lombok.NonNull;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.Helper;
import winetavern.model.management.Shift;
import winetavern.model.management.ShiftRepository;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * @author Louis
 */

@Controller
public class ShiftController {
    @NonNull @Autowired private ShiftRepository shifts;
    @NonNull @Autowired private EmployeeManager employees;
    @NonNull @Autowired private BusinessTime time;

    @RequestMapping("/admin/management/shifts")
    public String showShifts(Model model) {
        List<Employee> list = Helper.convertToList(employees.findAll());

        TimeInterval week = getWeekInterval(time.getTime()); //get the week interval out of businessTime
        List<Shift> shiftsOfWeek = getShiftsOfWeek(week);    //get all shifts in this interval

        model.addAttribute("shifts", shiftsOfWeek);
        model.addAttribute("calendarString", buildCalendarString());

        return "shifts";
    }

    /**
     * compiles all shifts into a String which can be parsed into an Object by JSON (javascript) and then put into the
     * calendar.
     * @return JSON parsable String
     */
    private String buildCalendarString() {
        String calendarString = "[";
        boolean noComma = true;
        Map<Employee, String> colorMap = getColorMap();

        for (Shift shift : shifts.findAll()) { //add all shifts
            if (noComma)
                noComma = false;
            else
                calendarString = calendarString + ",";

            TimeInterval interval = shift.getInterval();
            calendarString = calendarString +
                    "{\"title\":\"" + shift.getEmployee().getUserAccount().getFirstname().substring(0, 1) +
                                      shift.getEmployee().getUserAccount().getLastname().substring(0, 1) +
                    "\",\"start\":\"" + interval.getStart() +
                    "\",\"end\":\"" + interval.getEnd() +
                    "\",\"color\":\"" + colorMap.get(shift.getEmployee()) +
                    "\",\"url\":\"" + "/admin/management/shifts/change/" + shift.getId() +
                    "\",\"description\":\"" + shift.getEmployee() + "<br/>" +
                                              shift.getEmployee().getDisplayNameOfRole() + "\"}";
        }

        return calendarString + "]";
    }

    private Map<Employee, String> getColorMap() {
        List<Employee> employeeList = Helper.convertToList(employees.findAll());
        Map<Employee, String> res = new HashMap<>();

        for (int i = 0; i < employeeList.size(); i++) {
            Color c = Color.getHSBColor((float) i / employeeList.size(), 1, 1);
            res.put(employeeList.get(i), "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")");
        }

        return res;
    }

    @RequestMapping("/admin/management/shifts/add")
    public String addShiftData(Model model){
        model.addAttribute("calendarString", buildCalendarString());
        model.addAttribute("employees",employees.findAll());
        model.addAttribute("time",getTimes());
        return "shifts";
    }

    @PostMapping("/admin/management/shifts/add")
    public String addShift(@RequestParam("employee") Long employeeId,
                           @RequestParam("date") String dateString, @RequestParam("start") String startString,
                           @RequestParam("end") String endString) {

        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalTime start = LocalTime.parse(startString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endString, DateTimeFormatter.ofPattern("HH:mm"));

        shifts.save(new Shift(new TimeInterval(date.atTime(start), date.atTime(end)), employees.findOne(employeeId).get()));
        return "redirect:/admin/management/shifts";
    }

    @RequestMapping("/admin/management/shifts/change/{shiftid}")
    public String changeShift(@PathVariable Long shiftid, Model model) {
        Shift shift = shifts.findOne(shiftid).get();
        model.addAttribute("shiftdata",shift);
        model.addAttribute("date", Helper.localDateTimeToDateString(shift.getInterval().getStart()));
        model.addAttribute("time",getTimes());
        model.addAttribute("employees",employees.findAll());
        model.addAttribute("calendarString", buildCalendarString());
        return "shifts";
    }

    @PostMapping("/admin/management/shifts/change/{shiftid}")
    public String changeShift(@PathVariable Long shiftid, @RequestParam("employee") Long employeeId,
                              @RequestParam("date") String dateString, @RequestParam("start") String startString,
                              @RequestParam("end") String endString){

        Shift shift = shifts.findOne(shiftid).get();

        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalTime start = LocalTime.parse(startString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endString, DateTimeFormatter.ofPattern("HH:mm"));

        shift.setEmployee(employees.findOne(employeeId).get());
        shift.setInterval(new TimeInterval(date.atTime(start), date.atTime(end)));

        shifts.save(shift);

        return "redirect:/admin/management/shifts";
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
