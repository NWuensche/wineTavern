package winetavern;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.accountancy.Expense;
import winetavern.model.user.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public static String localDateTimeToDateString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    public static String localDateTimeToDateTimeString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    public static String localDateTimeToJavascriptDateString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss").withLocale(new Locale("en"));
        return dateTime.format(formatter);
    }
}
