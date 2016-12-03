package winetavern;

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

}
