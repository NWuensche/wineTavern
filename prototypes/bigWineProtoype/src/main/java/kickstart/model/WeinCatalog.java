package kickstart.model;

import org.salespointframework.catalog.Catalog;

/**
 * An extension of {@link Catalog} to add video shop specific query methods.
 *
 * @author Oliver Gierke
 */
public interface WeinCatalog extends Catalog<Wein> {

    /**
     * Returns all {@link Wein}s by type.
     *
     *
     * @return
     */
    Iterable<Wein> findByName(String name);
}
