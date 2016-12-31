package winetavern.model.management;

import org.salespointframework.catalog.Catalog;
import winetavern.ExtraCatalogRepository;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Louis
 */
public interface EventCatalog extends ExtraCatalogRepository<Event> {
    LinkedList<Event> findByVintnerDayTrue();
    HashSet<Event> findAll();
}
