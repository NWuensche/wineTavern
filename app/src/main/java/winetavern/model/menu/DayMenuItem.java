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

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private MonetaryAmount price;
    private Product product;
    private String name;
    private String description;
    private boolean enabled;

    @ManyToOne(targetEntity = DayMenu.class)
    private DayMenu dayMenu;

    public DayMenuItem() {};

    public DayMenuItem(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public DayMenuItem(String name, String description, Money price) {
        this.name = name;
        this.price = price;
        this.description = description;
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

}
