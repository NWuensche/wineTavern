package winetavern.controller;

import static org.salespointframework.core.Currencies.EURO;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.stock.Category;
import winetavern.model.stock.ProductCatalog;
import winetavern.model.user.Roles;

/**
 * @author Louis
 */
public class StockControllerWebIntegrationTests extends AbstractWebIntegrationTests {
    @Autowired private Inventory<InventoryItem> stock;
    @Autowired private ProductCatalog products;
    private Product product;
    private InventoryItem inventoryItem;

    @Before
    public void before() {
        product = new Product("Äpfel", Money.of(1.99, EURO));
        product.addCategory(Category.SNACK.toString());

        inventoryItem = new InventoryItem(product, Quantity.of(3.0));
        stock.save(inventoryItem);
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

        Product newProduct = new Product("Birnen", Money.of(1.99, EURO));
        newProduct.addCategory(Category.SNACK.toString());

        stock.save(new InventoryItem(newProduct, Quantity.of(5)));
        assertTrue(stock.findByProduct(newProduct).isPresent());
    }

    @Test
    public void manageStockRight() throws Exception {
        RequestBuilder request = post("/admin/stock")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("productAmount"))
                .andExpect(view().name("stock"));
    }

    @Test
    public void detailItemRight() throws Exception {
        RequestBuilder request = post("/admin/stock/details/" + inventoryItem.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attribute("product", product))
                .andExpect(view().name("stock"));
    }

    @Test
    public void addProductRight() throws Exception {
        RequestBuilder request = post("/admin/stock/addProduct/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("name", "prod")
                .param("price", "3.4")
                .param("category", Category.SNACK.toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(Helper.getFirstItem(products.findByName("prod")).getPrice(), is(Money.of(3.4, EURO)));
    }

    @Test
    public void changeProductRight() throws Exception {
        RequestBuilder request = post("/admin/stock/changeProduct/")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("productid", product.getId().toString())
                .param("productname", "Schnaps")
                .param("productprice", "10")
                .param("productcategory", Category.LIQUOR.toString());

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(products.findByName("Äpfel").spliterator().getExactSizeIfKnown(), is(0l));
        assertThat(Helper.getFirstItem(products.findByName("Schnaps")).getPrice(), is(Money.of(10, EURO)));
    }

}
