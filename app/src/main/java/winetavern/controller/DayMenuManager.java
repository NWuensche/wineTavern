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
import winetavern.model.stock.ProductCatalog;

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

    @RequestMapping("/admin/daymenulist")
    public ModelAndView showMenus(ModelAndView modelAndView) {
        return showMenuList(modelAndView);
    }

    @RequestMapping("/admin/addMenu")
    public String addMenu(Model model) {
        DateParameter dateParameter = new DateParameter();
        model.addAttribute("date", dateParameter);
        return "addmenu";
    }

    /**
     * @param dateParameter if day starts with a 0, the month will be count up by one
     */
    @RequestMapping(value = "/admin/addMenu", method = RequestMethod.POST)
    public ModelAndView addMenuPost(@ModelAttribute(value = "date") DateParameter dateParameter,
                                    ModelAndView modelAndView) {
        dateParameter.setMonth(dateParameter.getMonth()-1); //workaround, see above
        Calendar creationDate = dateParameter.getCalendar();
        DayMenu dayMenu = new DayMenu(creationDate);
        dayMenuRepository.save(dayMenu);

        return showMenuList(modelAndView);
    }

    @RequestMapping(value = "/admin/removeMenu", method = RequestMethod.POST)
    public ModelAndView removeMenu(@RequestParam("daymenuid") Long dayMenuId, ModelAndView modelAndView) {
        DayMenu dayMenu = dayMenuRepository.findById(dayMenuId);
        if(dayMenu != null) {
            dayMenuRepository.delete(dayMenu);
        }
        return showMenuList(modelAndView);
    }

    @RequestMapping("/admin/editMenu")
    public String editMenu(@RequestParam("id") Long id, Model model) {
        DayMenu dayMenu = dayMenuRepository.findById(id);
        model.addAttribute("daymenu", dayMenu);
        model.addAttribute("stock", stock);
        model.addAttribute("frommenuitemid", id);
        return "editdaymenu";
    }

    @RequestMapping("/admin/menu/getDayMenuByDay")
    public String getDayMenuByDay() {
        return "daymenu";
    }

    public ModelAndView showMenuList(ModelAndView modelAndView) {
        Iterable<DayMenu> dayMenuList = dayMenuRepository.findAll();
        modelAndView.addObject("menus", dayMenuList);
        modelAndView.setViewName("daymenulist");
        return modelAndView;
    }

}
