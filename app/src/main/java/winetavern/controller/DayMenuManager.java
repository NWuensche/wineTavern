package winetavern.controller;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.DateParameter;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuRepository;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by Michel on 11/4/2016.
 * @author Michel Kunkler
 */

@Controller
public class DayMenuManager {

    private DayMenuRepository dayMenuRepository;
    private final Inventory<InventoryItem> stock;

    @Autowired
    public DayMenuManager(Inventory<InventoryItem> stock, DayMenuRepository dayMenuRepository) {
        this.stock = stock;
        this.dayMenuRepository = dayMenuRepository;
    }

    @RequestMapping("/admin/menu/show")
    public ModelAndView showMenus(ModelAndView modelAndView) {
        return showMenuList(modelAndView);
    }

    @RequestMapping("/admin/menu/add")
    public String addMenu(Model model) {
        DateParameter dateParameter = new DateParameter();
        model.addAttribute("date", dateParameter);
        return "addmenu";
    }

    @RequestMapping(value = "/admin/menu/add", method = RequestMethod.POST)
    public ModelAndView addMenuPost(@ModelAttribute(value = "date") DateParameter dateParameter,
                                    ModelAndView modelAndView) {
        dateParameter.setMonth(dateParameter.getMonth());
        LocalDate creationDate = dateParameter.getDate();
        DayMenu dayMenu = new DayMenu(creationDate);
        dayMenuRepository.save(dayMenu);

        return showMenuList(modelAndView);
    }

    @RequestMapping(value = "/admin/menu/remove", method = RequestMethod.POST)
    public ModelAndView removeMenu(@RequestParam("daymenuid") Long dayMenuId, ModelAndView modelAndView) {
        DayMenu dayMenu = dayMenuRepository.findOne(dayMenuId).get();
        if(dayMenu != null) {
            dayMenuRepository.delete(dayMenu);
        }
        return showMenuList(modelAndView);
    }

    @RequestMapping("/admin/menu/edit/{pid}")
    public String editMenu(@PathVariable("pid") Long id, Model model) {
        DayMenu dayMenu = dayMenuRepository.findOne(id).get();
        model.addAttribute("daymenu", dayMenu);
        model.addAttribute("stock", stock);
        model.addAttribute("frommenuitemid", id);
        return "editdaymenu";
    }

    public ModelAndView showMenuList(ModelAndView modelAndView) {
        Iterable<DayMenu> dayMenuList = dayMenuRepository.findAll();
        modelAndView.addObject("menus", dayMenuList);
        modelAndView.setViewName("daymenulist");
        return modelAndView;
    }

}
