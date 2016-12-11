package winetavern.model.management;

import org.salespointframework.catalog.Catalog;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Louis
 */
public interface EventCatalog extends Catalog<Event> {
    LinkedList<Event> findByVintnerDayTrue();
    HashSet<Event> findAll();
}
