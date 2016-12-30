package winetavern.model.reservation;

import lombok.*;
import org.junit.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

/**
 * @author Sev
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
@Getter
public class Desk {

    @Id @GeneratedValue private long id;

    @OneToMany(targetEntity=Reservation.class, mappedBy="desk", fetch = FetchType.EAGER)
    private List<Reservation> reservationList;

    @Setter private String name;
    private int capacity;

    public Desk(String name, int capacity) throws IllegalArgumentException {
        if(capacity <= 0) {
            throw new IllegalArgumentException ("No Desk should have capacity <= 0");
        }

        this.name = name;
        this.capacity = capacity;
        this.reservationList = new ArrayList<>();
    }

    public void setCapacity(int capacity) {
        Assert.assertThat(capacity, greaterThan(0));
        this.capacity = capacity;
    }

    protected void addReservation(Reservation reservation) {
        this.reservationList.add(reservation);
    }

}
