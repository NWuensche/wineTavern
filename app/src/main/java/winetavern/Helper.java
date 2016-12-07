package winetavern;

import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.ExternalManager;
import winetavern.model.user.Person;

import java.util.ArrayList;
import java.util.List;

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

}
