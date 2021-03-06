package winetavern;

import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.model.accountancy.Expense;
import winetavern.model.user.*;

import javax.money.MonetaryAmount;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Niklas Wünsche
 */

public interface Helper {

    static List<Person> findAllPersons(EmployeeManager employeeManager, ExternalManager externalManager) {
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
    static Expense findOne(String id, Accountancy accountancy) {
        for (AccountancyEntry exp : accountancy.findAll()) {
            if (id.equals(exp.getId().toString())) {
                return ((Expense) exp);
            }
        }
        return null;
    }

    static String localDateTimeToDateString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    static String localDateTimeToDateTimeString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    static String localDateTimeToTimeString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    static String localDateTimeToJavascriptDateString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss").withLocale(new Locale("en"));
        return dateTime.format(formatter);
    }

    static String moneyToEuroString(MonetaryAmount money) {
        if(money == null) {
            return "0 €";
        }

        String res = Math.round(money.getNumber().doubleValue() * 100) / 100.0 + "";
        String[] splittetValue = res.split("\\.");

        if (splittetValue[1].length() == 1)
            return res + "0 €";

        return res + " €";
    }


    /**
     * returns whether the given date is a vintner day or not (first friday every uneven month)
     * @param date
     * @return boolean is vintner day
     */
    //TODO Can this be deleted?
    static boolean isVintnerDay(LocalDate date) {
        return (date.getDayOfWeek() == DayOfWeek.FRIDAY) && ((date.getMonthValue() % 2) == 1) && (date.getDayOfMonth() < 6);
    }

}
