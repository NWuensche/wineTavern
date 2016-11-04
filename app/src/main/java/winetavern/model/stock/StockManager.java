package winetavern.model.stock;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Louis
 */
public class StockManager {
    private static StockManager instance;
    private StockItem stock;

    public StockManager() {
        this.stock = new ProductCategory("Lager");
    }

    public static StockManager getInstance() {
        if (StockManager.instance == null)
            StockManager.instance = new StockManager();
        return StockManager.instance;
    }

    public StockItem getStock() {
        return stock;
    }

    public Optional<Product> getProductByName(String name) {
        return null;
    }

    public Set<Product> getAllProducts() {
        return stock.getAllProducts();
    }
}
