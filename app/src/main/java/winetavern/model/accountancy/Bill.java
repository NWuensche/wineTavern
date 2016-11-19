package winetavern.model.accountancy;

import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import winetavern.model.user.Person;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.salespointframework.core.Currencies.EURO;

/**
 * @author Louis
 */

@Entity
public class Bill {
    @GeneratedValue @Id private long id;
    private String desk;
    private boolean isClosed = false;
    private MonetaryAmount totalPrice = Money.of(0, EURO);
    private LocalDateTime payTime;
    @ManyToOne private Person staff;
    @OneToMany(cascade=CascadeType.ALL) private Set<BillItem> items = new HashSet<>();

    @Deprecated
    protected Bill() {}

    public Bill(String desk, Person staff) {
        this.desk = desk;
        this.staff = staff;
    }

    public long getId() {
        return id;
    }

    public void changeItem(BillItem item, int quantity) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        if (item == null) throw new NullPointerException("the item must not be null");
        if (quantity == 0) {
            items.remove(item);
        } else {
            item.changeQuantity(quantity);
            if (!items.contains(item)) items.add(item);
        }
        reloadTotalPrice();
    }

    private void reloadTotalPrice() {
        MonetaryAmount sum = Money.of(0, EURO);
        for (BillItem item : items) sum = sum.add(item.getPrice());
        totalPrice = sum;
    }

    public String getDesk() {
        return desk;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void close(BusinessTime businessTime) {
        if (isClosed) throw new IllegalStateException("Bill is already closed");
        if (businessTime == null) System.out.println("businessTime is null!");
        this.payTime = businessTime.getTime();
        isClosed = true;
    }

    public LocalDateTime getCloseTime() {
        if (!isClosed()) throw new IllegalStateException("the bill must be closed to consist a time");
        return payTime;
    }

    public MonetaryAmount getPrice() {
        return totalPrice;
    }

    public Set<BillItem> getItems() {
        return items;
    }

    public Person getStaff() {
        return staff;
    }
}
