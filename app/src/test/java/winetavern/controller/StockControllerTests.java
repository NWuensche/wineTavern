package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.stock.ProductCatalog;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;

/**
 * @author Louis
 */

@Transactional
public class StockControllerTests extends AbstractWebIntegrationTests {
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ProductCatalog products;

    private Product product = new Product("Äpfel", Money.of(1.99, EURO));

    @Test
    public void addProductTest() {
        products.save(product);
        assertTrue(products.exists(product.getId()));
    }

    @Test
    public void addInventoryItemTest() {
        stock.save(new InventoryItem(product, Quantity.of(5)));
        assertTrue(stock.findByProduct(product).isPresent());
    }
}
