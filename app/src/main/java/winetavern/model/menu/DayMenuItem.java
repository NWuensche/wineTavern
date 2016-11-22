package winetavern.model.menu;

import javax.money.MonetaryAmount;
import javax.persistence.*;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import java.util.LinkedList;
import java.util.List;

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

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = DayMenu.class, cascade = CascadeType.ALL)
    @JoinTable (joinColumns = {@JoinColumn(name="day_menu_id")},
            inverseJoinColumns = {@JoinColumn(name = "day_menu_item_id")}
    )
    private List<DayMenu> dayMenus;

    public DayMenuItem() {
        this.dayMenus = new LinkedList<DayMenu>();
    };

    public DayMenuItem(String name, Money price) {
        this.name = name;
        this.price = price;
        this.dayMenus = new LinkedList<DayMenu>();
    }

    public DayMenuItem(String name, String description, Money price, Double quantityPerProduct) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantityPerProduct = quantityPerProduct;
        this.dayMenus = new LinkedList<DayMenu>();
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

    public void setPrice(MonetaryAmount price) {
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

    public void setDayMenus(List<DayMenu> dayMenus) {
        this.dayMenus = dayMenus;
    }

    public List<DayMenu> getDayMenus() {
        return dayMenus;
    }

    public void addDayMenu(DayMenu dayMenu) {
        dayMenus.add(dayMenu);
    }

    public Double getQuantityPerProduct() {
        return quantityPerProduct;
    }

    public void setQuantityPerProduct(Double quantityPerProduct) {
        this.quantityPerProduct = quantityPerProduct;
    }

    public void removeDayMenu(DayMenu dayMenu) {
        this.dayMenus.remove(dayMenu);
    }

}
