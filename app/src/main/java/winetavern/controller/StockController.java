package winetavern.controller;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
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

import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */
@Controller
public class StockController {
    private final Inventory<InventoryItem> stock;
    private final ProductCatalog products;

    @Autowired
    public StockController(Inventory<InventoryItem> stock, ProductCatalog products) {
        this.stock = stock;
        this.products = products;
    }

    @RequestMapping("/admin/stock")
    public String manageStock(Model model) {
        //FOR TESTING ONLY
        if (stock.count() < 2) {
            stock.save(new InventoryItem(new Product("Vodka", Money.of(12.50, EURO)), Quantity.of(15)));
            stock.save(new InventoryItem(new Product("Berliner Brandstifter", Money.of(33.99, EURO)), Quantity.of(93)));
        }
        //END

        model.addAttribute("productAmount", stock.count());
        model.addAttribute("stockItems", stock.findAll());
        return "stock";
    }

    @RequestMapping("/admin/stock/details/{pid}")
    public String detail(@PathVariable("pid") InventoryItem stockItem, Model model) {

        model.addAttribute("product", stockItem.getProduct());
        model.addAttribute("quantity", stockItem.getQuantity());
        model.addAttribute("categories", stockItem.getProduct().getCategories());

        return "stock";
    }

    @RequestMapping(value = "/admin/stock/add", method = RequestMethod.POST)
    public String addProduct(@ModelAttribute("product") Product product, @ModelAttribute("quantity") Quantity quantity) {
        stock.save(new InventoryItem(product, quantity));
        return "redirect:/admin/stock";
    }

    @RequestMapping(value = "/admin/stock/increaseQuantity", method = RequestMethod.POST)
    public String increaseQuantity(@ModelAttribute("product") ProductIdentifier productId,
                                   @ModelAttribute("quantity") String quantityNumber) {
        Optional<Product> product = products.findOne(productId);
        if (product.isPresent()) {
            Optional<InventoryItem> item = stock.findByProduct(product.get());
            if (item.isPresent()) item.get().increaseQuantity(Quantity.of(new Integer(quantityNumber)));
        }
        return "redirect:/admin/stock";
    }

    @RequestMapping(value = "/admin/stock/decreaseQuantity", method = RequestMethod.POST)
    public String decreaseQuantity(@ModelAttribute("product") ProductIdentifier productId,
                                   @ModelAttribute("quantity") String quantityNumber) {
        Optional<Product> product = products.findOne(productId);
        if (product.isPresent()) {
            Optional<InventoryItem> item = stock.findByProduct(product.get());
            if (item.isPresent()) item.get().decreaseQuantity(Quantity.of(new Integer(quantityNumber)));
        }
        return "redirect:/admin/stock";
    }
}
