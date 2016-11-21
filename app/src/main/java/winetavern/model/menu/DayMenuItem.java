package winetavern.model.menu;

import javax.money.MonetaryAmount;
import javax.persistence.*;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

/**
 * Created by Michel on 11/3/2016.
 */

@Entity
public class DayMenuItem {

    @Id @GeneratedValue
    private Long id;
    private MonetaryAmount price;

    @OneToOne(fetch=FetchType.EAGER, targetEntity = Product.class)
    @JoinColumn(name = "product_id")
    private Product product;
    private String name;
    private String description;
    /**
     * Amount of this DayMenuItem you get out of one Product.
     */
    private Double quantityPerProduct;
    private boolean enabled;

    @ManyToOne(fetch=FetchType.EAGER, targetEntity = DayMenu.class)
    @JoinColumn(name = "day_menu_id")
    private DayMenu dayMenu;

    public DayMenuItem() {};

    public DayMenuItem(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public DayMenuItem(String name, String description, Money price, Double quantityPerProduct) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantityPerProduct = quantityPerProduct;
    }



    public Long getId() {
        return id;
    }

    public MonetaryAmount getPrice() {
        return price;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable()  {
        this.enabled = false;
    }

    public void setDayMenu(DayMenu dayMenu) {
        this.dayMenu = dayMenu;
    }

    public DayMenu getDayMenu() {
        return dayMenu;
    }

    public Double getQuantityPerProduct() {
        return quantityPerProduct;
    }

    public void setQuantityPerProduct(Double quantityPerProduct) {
        this.quantityPerProduct = quantityPerProduct;
    }

}
