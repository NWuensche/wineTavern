package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.stock.ProductCatalog;

import javax.money.MonetaryAmount;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller responsible for DayMenuItem's creation and managing.
 * @author Michel Kunkler
 */
@Controller
public class DayMenuItemManager {

    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private ProductCatalog productCatalog;
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private DayMenuRepository dayMenuRepository;


    /**
     * Initially called for adding a menu item.
     * Data gets processed in  {@link #addMenuItemPost(Product, String, Money, String, Double, Boolean, Long, ModelAndView)}
     */
    @RequestMapping("/admin/menuitem/add")
    public String addMenuItem(Model model, @RequestParam("frommenuitemid") Long cameFrom) {
        model.addAttribute("daymenuitems", dayMenuRepository.findOne(cameFrom).get()
                .getNotAddedDayMenuItems(dayMenuItemRepository.findByEnabled(true)));
        model.addAttribute("dayMenu", dayMenuRepository.findOne(cameFrom).get());
        model.addAttribute("stock", stock);
        return "addmenuitem";
    }

    /**
     *
     */
    @RequestMapping("/admin/menuitem/edit/{daymenuid}/{itemid}")
    public String editMenuItem(@PathVariable("daymenuid") Long dayMenuId,
                               @PathVariable("itemid") Long itemId,
                               Model model) {
        if (!pathVariablesValid(dayMenuId, itemId)) {
            return "error";
        }
        model.addAttribute("menuitem", dayMenuItemRepository.findOne(itemId).get());
        model.addAttribute("menu", dayMenuRepository.findOne(dayMenuId).get());
        model.addAttribute("stock", stock);
        return "editmenuitem";
    }

    private boolean pathVariablesValid(Long dayMenuId, Long dayMenuItemId) {
        Optional<DayMenuItem> optionalDayMenuItem = dayMenuItemRepository.findOne(dayMenuItemId);
        Optional<DayMenu> optionalDayMenu = dayMenuRepository.findOne(dayMenuId);

        return optionalDayMenuItem.isPresent() && optionalDayMenu.isPresent();
    }

    @RequestMapping(value = "/admin/menuitem/edit/{daymenuid}/{itemid}", method = RequestMethod.POST)
    public ModelAndView editMenuItemPost(@PathVariable("daymenuid") Long dayMenuId,
                                         @PathVariable("itemid") Long itemId,
                                         @RequestParam("product") Product product,
                                         @RequestParam("name") String name,
                                         @RequestParam("price") Money price,
                                         @RequestParam("description") String description,
                                         @RequestParam("quantityPerProduct") Double quantityPerProduct,
                                         @RequestParam("enabled") Boolean enabled,
                                         ModelAndView mvc) {
        Optional<DayMenuItem> optionalDayMenuItem = dayMenuItemRepository.findOne(itemId);
        Optional<DayMenu> optionalDayMenu = dayMenuRepository.findOne(dayMenuId);
        if (!pathVariablesValid(dayMenuId, itemId)) {
            mvc.setViewName("error");
            return mvc;
        }

        DayMenuItem dayMenuItem = optionalDayMenuItem.get();
        DayMenu dayMenu = optionalDayMenu.get();
        if(dayMenuItem.getDayMenus().size() > 1) {
            // remove connection from old daymenuitem to daymenu
            dayMenu.removeMenuItem(dayMenuItem);

            // clone dayMenuItem
            dayMenuItem = dayMenuItem.clone(dayMenu);
        }

        // change attributes
        dayMenuItem.setProduct(product);
        dayMenuItem.setName(name);
        dayMenuItem.setDescription(description);
        dayMenuItem.setPrice(price);
        dayMenuItem.setQuantityPerProduct(quantityPerProduct);
        dayMenuItem.setEnabled(enabled);
        dayMenuItemRepository.save(dayMenuItem);

        mvc.setViewName("redirect:/admin/menu/edit/"+dayMenu.getId());
        return mvc;
    }

    /**
     * The DayMenuItem adding takes two steps:
     * 1) choosing the corresponding Product from a list.
     * 2) setting DayMenuItem name, price and optionally a description.
     */
    @RequestMapping(value = "/admin/menuitem/add", method = RequestMethod.POST)
    public ModelAndView addMenuItemPost(@RequestParam("product") Product product,
                                        @RequestParam("name") String name,
                                        @RequestParam("price") Money price,
                                        @RequestParam("description") String description,
                                        @RequestParam("quantityPerProduct") Double quantityPerProduct,
                                        @RequestParam("enabled") Boolean enabled,
                                        @RequestParam("dayMenu") Long dayMenu,
                                        ModelAndView modelAndView) {

        DayMenuItem dayMenuItem = new DayMenuItem(name, description, price, quantityPerProduct);
        dayMenuItem.setProduct(product);
        dayMenuItem.setName(name);
        dayMenuItem.setPrice(price);
        dayMenuItem.setDescription(description);
        dayMenuItem.setQuantityPerProduct(quantityPerProduct);
        dayMenuItem.setEnabled(enabled);
        dayMenuRepository.findOne(dayMenu).get().addMenuItem(dayMenuItem);
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/menu/edit/"+String.valueOf(dayMenu));
        return modelAndView;
    }

    @RequestMapping(value = "/admin/menuitem/addExisting", method = RequestMethod.POST)
    public ModelAndView addMenuItemPostExisting(@RequestParam("daymenuitem") Long dayMenuItemId,
                                                @RequestParam("dayMenu") Long dayMenuId,
                                                ModelAndView modelAndView) {
        DayMenuItem dayMenuItem = dayMenuItemRepository.findOne(dayMenuItemId).get();
        DayMenu dayMenu = dayMenuRepository.findOne(dayMenuId).get();
        dayMenu.addMenuItem(dayMenuItem);
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/menu/edit/"+String.valueOf(dayMenuId));
        return modelAndView;
    }

    @RequestMapping("/admin/menuitem/remove/{daymenuid}/{itemid}")
    public ModelAndView removeDayMenuItemFromDayMenu(@PathVariable("daymenuid") Long dayMenuId,
                                                     @PathVariable("itemid") Long itemId,
                                                     ModelAndView modelAndView) {
        DayMenuItem dayMenuItem = dayMenuItemRepository.findOne(itemId).get();
        DayMenu dayMenu = dayMenuRepository.findOne(dayMenuId).get();
        dayMenu.removeMenuItem(dayMenuItem);
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/menu/edit/"+String.valueOf(dayMenuId));
        return modelAndView;
    }
}
