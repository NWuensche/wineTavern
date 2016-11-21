package winetavern.controller;

import org.javamoney.moneta.*;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.stock.ProductCatalog;

import javax.money.MonetaryAmount;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
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
    private Inventory<InventoryItem> stock;
    @Autowired
    private DayMenuRepository dayMenuRepository;


    /**
     * Initially called for adding a menu item.
     * Data gets processed in  {@link #addMenuItemPost(Product, String, MonetaryAmount, String, Double, Boolean, Long, ModelAndView)}
     */
    @RequestMapping("/admin/addMenuItem")
    public String addMenuItem(Model model, @RequestParam("frommenuitemid") Long cameFrom) {
        model.addAttribute("dayMenu", dayMenuRepository.findById(cameFrom));
        model.addAttribute("stock", stock.findAll());
        return "addmenuitem";
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
                DayMenu dayMenu = dayMenuRepository.findById(Long.parseLong(text));
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
    @RequestMapping(value = "/admin/addMenuItem", method = RequestMethod.POST)
    public ModelAndView addMenuItemPost(@RequestParam("product") Product product,
                                        @RequestParam("name") String name,
                                        @RequestParam("price") MonetaryAmount price,
                                        @RequestParam("description") String description,
                                        @RequestParam("quantityPerProduct") Double quantityPerProduct,
                                        @RequestParam("enabled") Boolean enabled,
                                        @RequestParam("dayMenu") Long dayMenu,
                                        ModelAndView modelAndView) {

        DayMenuItem dayMenuItem = new DayMenuItem();
        dayMenuItem.setProduct(product);
        dayMenuItem.setName(name);
        dayMenuItem.setPrice(price);
        dayMenuItem.setDescription(description);
        dayMenuItem.setQuantityPerProduct(quantityPerProduct);
        dayMenuItem.setEnabled(enabled);
        dayMenuItem.addDayMenu(dayMenuRepository.findById(dayMenu));
        dayMenuItemRepository.save(dayMenuItem);

        modelAndView.setViewName("redirect:/admin/editMenu?id="+String.valueOf(dayMenu));
        return modelAndView;
    }
}
