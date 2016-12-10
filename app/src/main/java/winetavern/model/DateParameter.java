package winetavern.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Michel
 */
@Getter
@Setter
public class DateParameter {

    private int day;
    private int month;
    private int year;

    public LocalDate getDate() {
        return LocalDate.of(year, month, day);
    }

}
