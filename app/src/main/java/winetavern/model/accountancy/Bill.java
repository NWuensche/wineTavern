package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Entity
public class Bill {
    @Transient @Autowired private BillItemRepository itemRepository;

    @GeneratedValue @Id private long id;
    private int desk;
    private boolean isClosed = false;
    @ManyToOne private Person staff;
    @OneToMany(cascade=CascadeType.ALL) private Set<BillItem> items = new HashSet<>();

    @Deprecated
    protected Bill() {}

    public Bill(int desk, Person staff) {
        this.desk = desk;
        this.staff = staff;
    }

    public long getId() {
        return id;
    }

    public boolean addItem(BillItem item) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        if (item == null) throw new NullPointerException("the item must not be null");
        return items.add(item);
    }

    public boolean removeItem(BillItem item) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        if (item == null) throw new NullPointerException("the item must not be null");
        return items.remove(item);
    }

    public int getDesk() {
        return desk;
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

    public void clear() {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        items.clear();
    }

    public Person getStaff() {
        return staff;
    }
}
