package kickstart.model.menu;

import java.util.*;
import javax.persistence.Entity;
import org.hibernate.annotations.Generated;
import org.joda.money.*;
import org.salespointframework.catalog.Product;
import javax.persistence.Id;
import org.springframework.data.repository.Repository;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Created by Michel on 11/3/2016.
 */
interface MenuItemRepository extends Repository<MenuItem, Long> {
    List<MenuItem> findById(Long id);
    List<MenuItem> findAll();
}

@Entity
public class MenuItem {

    @Id
    private long id;
    private Money price;
    private Product product;
    private String name;
    private String description;
    private boolean enabled;

    public void MenuItem(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public void MenuItem(String name, String description, Money price) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Money getPrice() {
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

}
