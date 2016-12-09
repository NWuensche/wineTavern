package winetavern.controller;

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
        model.addAttribute("productAmount", stock.count());
        model.addAttribute("stockItems", stock.findAll());
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
    public String addProduct(@ModelAttribute("name") String name, @ModelAttribute("price") String price, @ModelAttribute("category") String category) {
        Product newProduct = new Product(name, Money.of(Float.parseFloat(price), EURO));
        newProduct.addCategory(category);
        stock.save(new InventoryItem(newProduct, Quantity.of(1)));
        return "redirect:/admin/stock";
    }

    @RequestMapping(value = "/admin/stock/changeProduct", method = RequestMethod.POST)
    public String changeProduct(@ModelAttribute("productid") Product product,
                             @ModelAttribute("productname") String name,
                             @ModelAttribute("productprice") String price,
                             @ModelAttribute("productcategory") String category) {
        product.setName(name);

        product.setPrice(Money.of(Float.parseFloat(price), EURO));

        product = removeAllCategories(product);
        product.addCategory(category);

        products.save(product);

        return "redirect:/admin/stock";
    }

    private Product removeAllCategories(Product product) {
        product.getCategories().forEach(cat -> product.removeCategory(cat));
        return product;
    }
}
