package winetavern.controller;

import org.javamoney.moneta.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.stock.ProductCatalog;

import java.time.DayOfWeek;
import java.util.Map;


/**
 * Controller responsible for DayMenuItem's creation and managing.
 * @author Michel Kunkler
 */
@Controller
public class DayMenuItemManager {

    @Autowired
    private DayMenuItemRepository dayMenuItemRepository;
    @Autowired
    private ProductCatalog productCatalog;
    @Autowired
    private DayMenuRepository dayMenuRepository;


    @RequestMapping("/admin/addMenuItem")
    public String addMenuItem(Model model, @RequestParam("frommenuitemid") Long cameFrom) {
        DayMenuItem dayMenuItem = new DayMenuItem();
        dayMenuItem.setDayMenu(dayMenuRepository.findById(cameFrom));
        model.addAttribute("menuitem", dayMenuItem);
        model.addAttribute("products", productCatalog.findAll());
        model.addAttribute("frommenuitemid", cameFrom);
        return "addmenuitem";
    }

    /**
     * The DayMenuItem adding takes two steps:
     * 1) choosing the corresponding Product from a list.
     * 2) setting DayMenuItem name, price and optionally a description.
     */
    @RequestMapping(value = "/admin/addMenuItem", method = RequestMethod.POST)
    public ModelAndView addMenuItemPost(@ModelAttribute("menuitem") DayMenuItem dayMenuItem,
                                        @ModelAttribute("frommenuitemid") Long cameFrom,
                                        ModelAndView modelAndView) {

        if(dayMenuItem.getProduct() != null) {
            modelAndView.addObject("menuitem", dayMenuItem);
            modelAndView.addObject("frommenuitemid", cameFrom);
            modelAndView.setViewName("addmenuitem");
            return modelAndView;
        } else {
            dayMenuItemRepository.save(dayMenuItem);
            modelAndView.setViewName("redirect:/admin/editMenu?id="+String.valueOf(cameFrom));
            return modelAndView;
        }
    }
}
