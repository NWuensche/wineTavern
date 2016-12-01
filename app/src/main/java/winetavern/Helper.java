package winetavern;

import java.util.ArrayList;

/**
 * @author Niklas Wünsche
 */

public class Helper {

    public static <T> T[] convertToArray(Iterable<T> stream) {
        ArrayList<T> returnList = new ArrayList<T>();
        stream.forEach(item -> returnList.add(item));
        return (T[]) returnList.toArray();
    }

    public static <T> T getFirstItem(Iterable<T> stream) {
        return convertToArray(stream)[0];
    }

}
