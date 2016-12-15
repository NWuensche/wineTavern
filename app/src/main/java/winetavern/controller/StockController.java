package winetavern.controller;

import lombok.NonNull;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Vintner;
import winetavern.model.user.VintnerManager;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Controller
public class StockController {
    @NonNull @Autowired private VintnerManager vintnerManager;
    @NonNull @Autowired private Inventory<InventoryItem> stock;
    @NonNull @Autowired private ProductCatalog products;

    @RequestMapping("/admin/stock")
    public String manageStock(Model model) {
        model.addAttribute("productAmount", stock.count());
        model.addAttribute("stockItems", stock.findAll());
        model.addAttribute("vintners",vintnerManager.findAll());
        return "stock";
    }

    @RequestMapping("/admin/stock/details/{pid}")
    public String detail(@PathVariable("pid") InventoryItem stockItem, Model model) {

        model.addAttribute("product", stockItem.getProduct());
        model.addAttribute("quantity", stockItem.getQuantity());
        model.addAttribute("categories", stockItem.getProduct().getCategories()); // TODO Necessary?
        model.addAttribute("productCategory", stockItem.getProduct().getCategories().stream().findFirst().get());

        manageStock(model);

        return "stock";
    }

    @RequestMapping(value = "/admin/stock/addProduct", method = RequestMethod.POST)
    public String addProduct(@ModelAttribute("name") String name, @ModelAttribute("price") String price,
                             @ModelAttribute("category") String category, @ModelAttribute("vintner") Long vintnerId) {

        Product newProduct = new Product(name, Money.of(Float.parseFloat(price), EURO));
        newProduct.addCategory(category);
        stock.save(new InventoryItem(newProduct, Quantity.of(1)));

        if (category.contains("wein")) { //every wine must be specified with a vintner
            for (Vintner vintner : vintnerManager.findAll()) {
                if (vintner.getId().equals(vintnerId)) { //add wine to the selected vintner
                    vintner.addWine(newProduct);
                    vintnerManager.save(vintner);
                } else if (vintner.removeWine(newProduct)) { //remove wine from all other vintners
                    vintnerManager.save(vintner); //only need to save if vintner was changed
                }
            }
        }

        return "redirect:/admin/stock";
    }

    @RequestMapping(value = "/admin/stock/changeProduct", method = RequestMethod.POST)
    public String changeProduct(@ModelAttribute("productid") Product product,
                             @ModelAttribute("productname") String name,
                             @ModelAttribute("productprice") String price,
                             @ModelAttribute("productcategory") String category,
                             @ModelAttribute("productvintner") String vintner) {
        product.setName(name);

        product.setPrice(Money.of(Float.parseFloat(price), EURO));

        product = removeAllCategories(product);
        product.addCategory(category);

        //TODO catch vintner

        products.save(product);

        return "redirect:/admin/stock";
    }

    private Product removeAllCategories(Product product) {
        product.getCategories().forEach(cat -> product.removeCategory(cat));
        return product;
    }
}
