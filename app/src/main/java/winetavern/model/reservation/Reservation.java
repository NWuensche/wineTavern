package winetavern.model.reservation;

import com.mysql.cj.core.exceptions.NumberOutOfRange;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import winetavern.model.management.TimeInterval;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for a single reservation
 * @author Sev
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@Deprecated}))
@Getter
public class Reservation {

    @Id @GeneratedValue private long id;

    @ManyToOne(targetEntity=Desk.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "desk_id")
    private Desk desk;

    @Setter @OneToOne(cascade = {CascadeType.ALL}) private TimeInterval interval;
    @Setter private String guestName;
    private int persons;

    public Reservation(String guestName, int persons, Desk desk, TimeInterval interval) {
        if(persons <= 0) {
            throw new NumberOutOfRange("At least 1 person should reserve!");
        }

        this.guestName = guestName;
        this.persons = persons;
        this.setDesk(desk);
        this.interval = interval;
    }

    public void setDesk(Desk desk) {
        if(this.desk != null) { // Dont do this in Constructor
            this.desk.getReservationList().remove(this); // Delete this reservation from the old desk
        }

        this.desk = desk;
        desk.addReservation(this);
    }

    public void setPersons(int numberOfPersons) {
        if(numberOfPersons <= 0) {
            throw new NumberOutOfRange("At least 1 person should reserve!");
        }

        this.persons = numberOfPersons;
    }

    public LocalDateTime getReservationStart() {
        return interval.getStart();
    }
    public LocalDateTime getReservationEnd() {
        return interval.getEnd();
    }

}
