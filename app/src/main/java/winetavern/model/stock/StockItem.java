package winetavern.model.stock;

import java.util.Set;

/**
 * @author Louis
 */
public abstract class StockItem {
    private String name;

    public StockItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Set<Product> getAllProducts();
}
