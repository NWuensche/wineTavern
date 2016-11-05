package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;


/**
 * Controller responsible for DayMenuItem's creation and managing.
 * @author Michel Kunkler
 */
@Controller
public class DayMenuItemManager {

    @Autowired
    private DayMenuItemRepository dayMenuItemRepository;


    @RequestMapping("/admin/addMenuItem")
    public String addMenuItem(Model model, @RequestParam("from") String cameFrom) {
        DayMenuItem dayMenuItem = new DayMenuItem();
        model.addAttribute("menuitem", dayMenuItem);
        model.addAttribute("camefrom", cameFrom);
        return "addmenuitem";
    }

    /**
     * The DayMenuItem adding takes two steps:
     * 1) choosing the corresponding Product from a list.
     * 2) setting DayMenuItem name, price and optionally a description.
     */
    @RequestMapping(value = "/admin/addMenuItem", method = RequestMethod.POST)
    public String addMenuItemPost(@ModelAttribute("menuitem") DayMenuItem dayMenuItem,
                                  @ModelAttribute("from") String cameFrom, Model model) {
        if (dayMenuItem.getProduct() == null ||
                dayMenuItem.getPrice() == null ||
                dayMenuItem.getName() == null) {
            model.addAttribute("menuitem", dayMenuItem);
            model.addAttribute("camefrom", cameFrom);
            return "addmenuitem";
        }
        if (cameFrom == null)
            return "/";
        else
            return cameFrom;
    }
}
