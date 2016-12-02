package winetavern.controller;

import lombok.NonNull;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Louis
 */

@Controller
public class SettingsController {
    @NonNull @Autowired private BusinessTime businessTime;

    @RequestMapping("/admin/settings")
    public String showSettings(Model model) {
        return "settings";
    }

    @PostMapping("/admin/settings")
    public String setBusinessTime(@RequestParam String time) {
        LocalDateTime newTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        LocalDateTime oldTime = businessTime.getTime();

        Duration duration = Duration.between(oldTime,newTime);
        businessTime.forward(duration);
        return "redirect:/admin/settings";
    }
}