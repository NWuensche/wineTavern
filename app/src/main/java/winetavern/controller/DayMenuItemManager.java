package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    private Inventory<InventoryItem> stock;
    @Autowired
    private DayMenuRepository dayMenuRepository;


    /**
     * Initially called for adding a menu item.
     * Data gets processed in  {@link #addMenuItemPost(Product, String, Money, String, Double, Boolean, Long, ModelAndView)}
     */
    // TODO How to call this one?
    @RequestMapping("/admin/menuitem/add")
    public String addMenuItem(Model model, @RequestParam("frommenuitemid") Long cameFrom) {
        model.addAttribute("daymenuitems", getNotAddedDayMenuItems(dayMenuItemRepository.findAll(),
                dayMenuRepository.findOne(cameFrom).get()));
        model.addAttribute("dayMenu", dayMenuRepository.findOne(cameFrom).get());
        model.addAttribute("stock", stock);
        return "addmenuitem";
    }

    /**
     * Returns a List of DayMenuItem's that are not in the givven DayMenu already
     * @param dayMenuItems
     * @param dayMenu
     * @return
     */

    // TODO Shouldn't this be a method of DayMenu?
    public List<DayMenuItem> getNotAddedDayMenuItems(Iterable<DayMenuItem> dayMenuItems, DayMenu dayMenu) {
        List<DayMenuItem> resultSet = new ArrayList<>(); // TODO Dont name a list resultSet
        dayMenuItems.forEach(dayMenuItem -> {
            if(!dayMenuItem.getDayMenus().contains(dayMenu))
                resultSet.add(dayMenuItem);
        });
        return resultSet;
    }


    /**
     * Custom Initbinder makes DayMenu and Product usable with form
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(DayMenu.class, "dayMenu", new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                DayMenu dayMenu = (DayMenu) getValue();
                return String.valueOf(dayMenu.getId());
            }
            @Override
            public void setAsText(String text) {
                DayMenu dayMenu = dayMenuRepository.findOne(Long.parseLong(text)).get();
                setValue(dayMenu);
            }
        });

        binder.registerCustomEditor(Product.class, "product", new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                Product product = (Product) getValue();
                if(product == null)
                    return "";
                return product.getId().toString();
            }
            @Override
            public void setAsText(String text) {
                Product product = productCatalog.findOne(text);
                setValue(product);
            }
        });
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
        dayMenuItem.addDayMenu(dayMenuRepository.findOne(dayMenu).get());
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
        dayMenuItem.addDayMenu(dayMenu);
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/menu/edit/"+String.valueOf(dayMenuId));
        return modelAndView;
    }

    @RequestMapping(value = "/admin/menuitem/removeFromDayMenu", method = RequestMethod.POST)
    public ModelAndView removeDayMenuItemFromDayMenu(@RequestParam("daymenuitem") Long dayMenuItemId,
                                                     @RequestParam("dayMenu") Long dayMenuId,
                                                     ModelAndView modelAndView) {
        DayMenuItem dayMenuItem = dayMenuItemRepository.findOne(dayMenuItemId).get();
        DayMenu dayMenu = dayMenuRepository.findOne(dayMenuId).get();
        dayMenuItem.removeDayMenu(dayMenu);
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/menu/edit/"+String.valueOf(dayMenuId));
        return modelAndView;
    }
}
