package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.Set;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Entity
public class Bill {
    @Transient @Autowired private BillItemRepository itemRepository;

    @GeneratedValue @Id private long id;
    private int restaurant_table;
    private boolean isClosed = false;
    @ManyToOne private Person staff;
    @OneToMany(cascade=CascadeType.ALL) private Set<BillItem> items;

    @Deprecated
    protected Bill() {}

    public Bill(int restaurant_table, Person staff) {
        this.restaurant_table = restaurant_table;
        this.staff = staff;
    }

    public long getId() {
        return id;
    }

    public boolean addItem(BillItem item) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        return items.add(item);
    }

    public void changeQuantity(BillItem item, int quantity) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        item.changeQuantity(quantity);
    }

    public boolean removeItem(BillItem item) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        return items.remove(item);
    }

    public int getRestaurant_table() {
        return restaurant_table;
    }

    public void close() {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        isClosed = true;
    }

    public MonetaryAmount getPrice() {
        MonetaryAmount res = Money.of(0, EURO);
        items.forEach(it -> res.add(it.getPrice()));
        return res;
    }

    public Set<BillItem> getItems() {
        return items;
    }
}
