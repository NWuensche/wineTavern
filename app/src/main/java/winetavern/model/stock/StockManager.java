package winetavern.model.stock;

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
        for (Product product : getAllProducts())
            if (product.getName().equals(name)) return Optional.of(product);
        return Optional.empty();
    }

    public Set<Product> getAllProducts() {
        return stock.getAllProducts();
    }
}
