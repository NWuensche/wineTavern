package winetavern.model.reservation;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Sev
 */
@Entity(name = "restaurant_table")
public class Table {
    @Id
    private int id;

    private int capacity;
    private int number;
    private int seats;

    public Table(int number, int seats) {
        this.number = number;
        this.seats = seats;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
