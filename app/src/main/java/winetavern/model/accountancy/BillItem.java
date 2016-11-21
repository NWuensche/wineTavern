package winetavern.model.accountancy;

import winetavern.model.menu.DayMenuItem;

import javax.money.MonetaryAmount;
import javax.persistence.*;

/**
 * @author Louis
 */

@Entity
public class BillItem implements Comparable<BillItem> {
    @GeneratedValue @Id private long id;
    @OneToOne(cascade = CascadeType.ALL) private DayMenuItem item;
    private int quantity;

    @Deprecated
    protected BillItem() {}

    public BillItem(DayMenuItem item) {
        if (item == null) throw new NullPointerException("the item must not be null");
        this.item = item;
        this.quantity = 0;
    }

    public MonetaryAmount getPrice() {
        return item.getPrice().multiply(quantity);
    }

    void changeQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("the quantity must not be negative");
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

    @Override
    public int compareTo(BillItem o) {
        return Long.compare(id, o.getId());
    }
}
