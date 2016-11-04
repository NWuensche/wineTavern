package winetavern.model.stock;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class OrderManager {
    private static OrderManager instance;
    private Set<Order> orders;

    public OrderManager() {
        this.orders = new HashSet<>();
    }

    public static OrderManager getInstance() {
        if (OrderManager.instance == null)
            OrderManager.instance = new OrderManager();
        return OrderManager.instance;
    }

    public boolean addOrder(Order order) {
        return orders.add(order);
    }

    public boolean removeOrder(Order order) {
        return orders.remove(order);
    }

    public Set<Order> getOrdersByState(Order.OrderState state) {
        Set<Order> res = new HashSet<>();
        for (Order order : orders)
            if (order.getState() == state) res.add(order);
        return res;
    }
}
