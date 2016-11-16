package winetavern.model.accountancy;

import org.salespointframework.catalog.Product;
import org.salespointframework.order.OrderLine;
import org.salespointframework.quantity.Quantity;
import winetavern.model.menu.DayMenuItem;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * @author Louis
 */

@Entity
public class DayMenuOrder extends OrderLine {
    @OneToOne private DayMenuItem item;

    public DayMenuOrder(Product product, Quantity quantity) {
        super(product, quantity);
    }

    public DayMenuOrder(DayMenuItem item, Quantity quantity) {
        super(item.getProduct(), quantity);
        this.item = item;
    }

    @Override
    public MonetaryAmount getPrice() {
        return item.getPrice().multiply(super.getQuantity().getAmount());
    }
}
