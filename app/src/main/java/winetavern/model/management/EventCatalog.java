package winetavern.model.management;

import org.salespointframework.catalog.Catalog;
import winetavern.ExtraCatalogRepository;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * @author Louis
 */
public interface EventCatalog extends ExtraCatalogRepository<Event> {
    LinkedList<Event> findByVintnerDayTrue();
    HashSet<Event> findAll();
}
