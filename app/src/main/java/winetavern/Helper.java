package winetavern;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.accountancy.Expense;
import winetavern.model.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Niklas Wünsche
 */

public class Helper {
    public static <T> List<T> convertToList(Iterable<T> iterable) {
        ArrayList<T> returnList = new ArrayList<T>();
        iterable.forEach(item -> returnList.add(item));
        return returnList;
    }

    public static <T> T[] convertToArray(Iterable<T> iterable) {
        return (T[]) convertToList(iterable).toArray();
    }

    public static <T> T getFirstItem(Iterable<T> stream) {
        return convertToArray(stream)[0];
    }

    public static List<Person> findAllPersons(EmployeeManager employeeManager, ExternalManager externalManager) {
        List<Person> res = new ArrayList<>();
        employeeManager.findAll().forEach(res::add);
        externalManager.findAll().forEach(res::add);
        return res;
    }

    /**
     * combines findOne() of the classes External and Employee (extends Person)
     * @see Person
     * @see External
     * @see Employee
     * @param personId          the id belonging to the person to find
     * @param employeeManager   the repository of the employees
     * @param externalManager   the repository of the externals
     * @return Optional<Person> present if there was a person with the given id
     */
    public static Optional<Person> findOnePerson(long personId, EmployeeManager employeeManager, ExternalManager externalManager) {
        Optional<Employee> optEmpl = employeeManager.findOne(personId);
        if (!optEmpl.isPresent()) {
            Optional<External> optExt = externalManager.findOne(personId);
            if (optExt.isPresent())
                return Optional.of(optExt.get());
        } else {
            return Optional.of(optEmpl.get());
        }
        return Optional.empty();
    }

    /**
     * returns an expense given by its id as a String (because Salespoint does not support that)
     * @see Expense extends AccountancyEntry
     * @param id          the AccountancyEntryIdentifier given as a String
     * @param accountancy the repository for the expenses
     * @return            the expense or null if the expense does not exist
     */
    public static Expense findOne(String id, Accountancy accountancy) {
        for (AccountancyEntry exp : accountancy.findAll()) {
            if (id.equals(exp.getId().toString())) {
                return ((Expense) exp);
            }
        }
        return null;
    }
}
