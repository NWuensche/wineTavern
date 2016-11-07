package winetavern.model.user;

import org.salespointframework.core.SalespointRepository;

import javax.persistence.Entity;

/**
 * Repository interface to handle {@link Person}s
 * @author Niklas Wünsche
 */


public interface PersonManager extends SalespointRepository<Person, Long> {

}
