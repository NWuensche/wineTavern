package winetavern;

import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Niklas Wünsche
 */
public interface ExtraCatalogRepository<T extends Product> extends Catalog<T> {

    default Stream<T> stream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

}
