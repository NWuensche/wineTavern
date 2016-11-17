package winetavern.model.accountancy;

import org.salespointframework.order.Order;

import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class Bill {
    @GeneratedValue @Id private long id;
    private int restaurant_table;
    @OneToOne(cascade = CascadeType.ALL) private Order order;

    @Deprecated
    protected Bill() {}

    public Bill(int restaurant_table, Order order) {
        this.restaurant_table = restaurant_table;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public int getRestaurant_table() {
        return restaurant_table;
    }

    public Order getOrder() {
        return order;
    }
}
