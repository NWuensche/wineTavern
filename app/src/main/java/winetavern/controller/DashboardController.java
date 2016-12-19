package winetavern.controller;

import lombok.NonNull;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import winetavern.model.management.ShiftRepository;

/**
 * @author Sev
 */

@Controller
public class DashboardController {
    @NonNull @Autowired
    BusinessTime time;
    @NonNull @Autowired
    ShiftRepository shifts;

    @RequestMapping("/dashboard")
    public String showDashboard(Model model){
        model.addAttribute("shifts",shifts.findAll());
        model.addAttribute("time",time);
        return "startadmin";
    }
}
