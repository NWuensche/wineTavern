package winetavern.controller;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.stock.Category;
import winetavern.model.stock.ProductCatalog;

/**
 * @author Louis
 */

@Transactional
public class StockControllerWebIntegrationTests extends AbstractWebIntegrationTests {
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ProductCatalog products;
    private Product product;

    @Before
    public void setUp() {
        product = new Product("Äpfel", Money.of(1.99, EURO));
        product.addCategory(Category.SNACK.toString());
    }

    @Test
    public void addProductTest() {
        products.save(product);

        assertTrue(products.exists(product.getId()));

        String savedProductCategory = products.findOne(product.getId()).get().getCategories().stream().findFirst().get();
        assertThat(savedProductCategory, is(Category.SNACK.toString()));
    }

    @Test
    public void addInventoryItemTest() {
        stock.save(new InventoryItem(product, Quantity.of(5)));
        assertTrue(stock.findByProduct(product).isPresent());
    }

}
