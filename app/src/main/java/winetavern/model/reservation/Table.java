package kickstart.model.reservation;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Michel on 11/3/2016.
 */
@Entity(name = "restaurant_table")
public class Table {
    @Id
    private int id;

    private int capacity;
    private int number;
}
