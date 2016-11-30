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
@Getter
public class External extends Person {

    @Id @GeneratedValue private Long id;
    @OneToOne private Event event;
    private String name;
    private MonetaryAmount wage;
    private boolean payed;

    @Deprecated
    protected External() {}

    public External(Event event, String name, MonetaryAmount wage) {
        this.event = event;
        this.name = name;
        this.wage = wage;
        payed = false;
    }

    public void pay() throws IllegalStateException{
        if(isPayed()) {
            throw new IllegalStateException("External was already payed!");
        }

        payed = true;
    }

}
