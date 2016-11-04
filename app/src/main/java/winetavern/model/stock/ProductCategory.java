package winetavern.model.stock;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */
public class ProductCategory extends StockItem{
    private Set<StockItem> stockItems;

    public ProductCategory(String name) {
        super(name);
        this.stockItems = new HashSet<>();
    }

    public boolean addStockItem(StockItem stockItem) {
        return stockItems.add(stockItem);
    }

    public boolean removeStockItem(StockItem stockItem) {
        return stockItems.remove(stockItem);
    }

    @Override
    public Set<Product> getAllProducts() {
        Set<Product> res = new HashSet<>();
        for (StockItem stockItem : stockItems)
            res.addAll(stockItem.getAllProducts());
        return res;
    }
}
