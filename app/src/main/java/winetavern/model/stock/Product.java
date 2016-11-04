package winetavern.model.stock;

import org.javamoney.moneta.Money;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class Product extends StockItem {
    private Money price;
    private int amount;

    public Product(String name, Money price, int amount) {
        super(name);
        this.price = price;
        this.amount = amount;
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public Set<Product> getAllProducts() {
        Set<Product> res = new HashSet<>();
        res.add(this);
        return res;
    }
}
