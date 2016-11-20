package winetavern.model.accountancy;

import winetavern.model.menu.DayMenuItem;

import javax.money.MonetaryAmount;
import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class BillItem {
    @GeneratedValue @Id private long id;
    @OneToOne(cascade = CascadeType.ALL) private DayMenuItem item;
    private int quantity;

    @Deprecated
    protected BillItem() {}

    public BillItem(DayMenuItem item) {
        this.item = item;
        this.quantity = 0;
    }

    public MonetaryAmount getPrice() {
        return item.getPrice().multiply(quantity);
    }

    protected void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity + " x " + item.getName();
    }

    public long getId() {
        return id;
    }

    public DayMenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
