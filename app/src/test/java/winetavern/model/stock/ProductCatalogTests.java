package winetavern.model.stock;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;

import static org.salespointframework.core.Currencies.EURO;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Niklas Wünsche
 */
public class ProductCatalogTests extends AbstractIntegrationTests {

    @Autowired private ProductCatalog productCatalog;
    private Product product;

    @Before
    public void before() {
        product = new Product("Schnaps", Money.of(3, EURO), Metric.LITER);
        product.addCategory(Category.LIQUOR.toString());
        productCatalog.save(product);
    }

    @Test
    public void setUpProductRight() {
        assertThat(product.getCategories().stream().findFirst().get(), is(Category.LIQUOR.toString()));
    }

    @Test
    public void findRightProduct() {
        assertThat(productCatalog.findOne(product.getId().toString()), is(product));
    }


}
