package winetavern.model.user;

import lombok.Getter;
import winetavern.model.management.Event;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Niklas Wünsche
 */

@Entity
public class External extends Person {

    @Id @GeneratedValue @Getter private Long id;
    @OneToOne @Getter private Event event;
    @Getter private String name;
    @Getter private MonetaryAmount wage;
    private boolean wagePayed;

    @Deprecated
    protected External() {}

    public External(Event event, String name, MonetaryAmount wage) {
        this.event = event;
        this.name = name;
        this.wage = wage;
        wagePayed = false;
    }

    public void pay() throws IllegalStateException{
        if(wasPayed()) {
            throw new IllegalStateException("External was already payed!");
        }

        wagePayed = true;
    }

    public boolean wasPayed() {
        return wagePayed;
    }

}
