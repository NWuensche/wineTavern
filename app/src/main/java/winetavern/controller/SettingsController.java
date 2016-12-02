package winetavern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Louis
 */

@Controller
public class SettingsController {
    @RequestMapping("/admin/settings")
    public String showSettings(Model model) {
        return "settings";
    }

    @PostMapping("/admin/settings")
    public String setBusinessTime(@RequestParam String time) {

        return "redirect:";
    }
}