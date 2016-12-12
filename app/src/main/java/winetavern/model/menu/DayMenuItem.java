package winetavern.model.menu;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;
import org.mockito.internal.stubbing.answers.ClonesArguments;
import org.salespointframework.catalog.Product;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Michel
 */

@Entity
@Getter
@Setter
public class DayMenuItem implements Cloneable {

    @Setter(value = AccessLevel.NONE) @Id @GeneratedValue private Long id;

    @OneToOne(fetch=FetchType.EAGER, targetEntity = Product.class)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany(fetch=FetchType.EAGER, targetEntity = DayMenu.class, cascade = CascadeType.ALL)
    @JoinTable (joinColumns = {@JoinColumn(name="day_menu_id")},
            inverseJoinColumns = {@JoinColumn(name = "day_menu_item_id")}
    )
    private List<DayMenu> dayMenus;

    /**
     * Amount of this DayMenuItem you get out of one Product.
     */
    private Double quantityPerProduct;

    private MonetaryAmount price;
    private String name;
    private String description;
    private boolean enabled;

    public DayMenuItem(String name, String description, Money price, Double quantityPerProduct) {
        this.dayMenus = new ArrayList<>();

        this.name = name;
        this.price = price;
        this.description = description;
        this.quantityPerProduct = quantityPerProduct;
        this.enabled = true;
    }

    public DayMenuItem() {
        dayMenus = new ArrayList<>();
    }

    // TODO Is this really necessary? shouldn't dayMenu.add(item) be enough?
    public void addDayMenu(DayMenu dayMenu) {
        dayMenus.add(dayMenu);
    }

    // TODO Is this really necessary? Shouldn't dayMenu.remove(item) be enough?
    public void removeDayMenu(DayMenu dayMenu) {
        dayMenus.remove(dayMenu);
    }

    public DayMenuItem clone(DayMenu dayMenu)  {
        DayMenuItem newDayMenuItem = new DayMenuItem();
        newDayMenuItem.addDayMenu(dayMenu);
        newDayMenuItem.setEnabled(enabled);
        newDayMenuItem.setDescription(description);
        newDayMenuItem.setName(name);
        newDayMenuItem.setPrice(price);
        newDayMenuItem.setProduct(product);
        newDayMenuItem.setQuantityPerProduct(quantityPerProduct);
        return newDayMenuItem;
    }

}
