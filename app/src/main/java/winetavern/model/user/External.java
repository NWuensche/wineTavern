package winetavern.model.user;

import winetavern.model.management.Event;

import javax.money.Monetary;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Niklas Wünsche
 */

public class External extends Person {

    @Id @GeneratedValue private Long id;

    @OneToOne private Event event;

    private String name;
    private Monetary wage;
    private boolean wagePayed;

    @Deprecated
    protected External() {}

    public External(Event event, String name, Monetary wage) {
        this.event = event;
        this.name = name;
        this.wage = wage;
        wagePayed = false;
    }

    public Event getEvent() {
        return event;
    }

    public String getName() {
        return name;
    }

    public Monetary getWage() {
        return wage;
    }

    public void payExternal() {
        if(wasPayed()) {
            throw new IllegalStateException("External was already payed!");
        }

        wagePayed = true;
    }

    public boolean wasPayed() {
        return wagePayed;
    }

}
