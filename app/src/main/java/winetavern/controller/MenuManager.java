package winetavern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Michel on 11/4/2016.
 */
@Controller
public class MenuManager {
    @RequestMapping("/admin/showMenus")
    public String showMenus() {
        return "daymenulist";
    }

    @RequestMapping("/admin/addMenu")
    public String addMenu() {
        return "daymenulist";
    }

    @RequestMapping("/admin/removeMenu")
    public String removeMenu() {
        return "daymenulist";
    }

    @RequestMapping("/admin/getDayMenuByDay")
    public String getDayMenuByDay() {
        return "daymenu";
    }
}
