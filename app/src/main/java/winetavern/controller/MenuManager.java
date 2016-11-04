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

    }

    @RequestMapping("/admin/addMenu")
    public String addMenu() {

    }

    @RequestMapping("/admin/removeMenu")
    public String removeMenu() {

    }

    @RequestMapping("/admin/getDayMenuByDay")
    public String getDayMenuByDay() {

    }
}
