package winetavern.model.stock;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public Set<Order> getEventsByTimespan(LocalDateTime time, Duration duration) {
        Set<Order> res = new HashSet<>();
        return res;
    }
}
