package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import winetavern.model.DateParameter;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuRepository;

import java.util.Calendar;

/**
 * Created by Michel on 11/4/2016.
 * @author Michel Kunkler
 */

@Controller
public class DayMenuManager {

    @Autowired
    private DayMenuRepository dayMenuRepository;

    @RequestMapping("/admin/daymenulist")
    public String showMenus(Model model) {
        return showMenuList(model);
    }

    @RequestMapping("/admin/addMenu")
    public String addMenu(Model model) {
        DateParameter dateParameter = new DateParameter();
        model.addAttribute("date", dateParameter);
        return "addmenu";
    }

    @RequestMapping(value = "/admin/addMenu", method = RequestMethod.POST)
    public String addMenuPost(@ModelAttribute(value = "date") DateParameter dateParameter, Model model) {
        Calendar creationDate = dateParameter.getCalendar();
        DayMenu dayMenu = new DayMenu(creationDate);
        dayMenuRepository.save(dayMenu);

        return showMenuList(model);
    }

    @RequestMapping("/admin/removeMenu")
    public String removeMenu(@RequestParam("id") long id, Model model) {
        DayMenu dayMenu = dayMenuRepository.findById(id);
        if(dayMenu == null) {
            return showMenuList(model);
        } else {
            dayMenuRepository.delete(dayMenuRepository.findById(id));
            return showMenuList(model);
        }
    }

    @RequestMapping("/admin/editMenu")
    public String editMenu(@RequestParam("id") Long id, Model model) {
        DayMenu dayMenu = dayMenuRepository.findById(id);
        model.addAttribute("daymenu", dayMenu);
        model.addAttribute("frommenuitemid", id);
        return "editdaymenu";
    }

    @RequestMapping("/admin/menu/getDayMenuByDay")
    public String getDayMenuByDay() {
        return "daymenu";
    }

    public String showMenuList(Model model) {
        Iterable<DayMenu> dayMenuList = dayMenuRepository.findAll();
        model.addAttribute("menus", dayMenuList);
        return "daymenulist";
    }

}
