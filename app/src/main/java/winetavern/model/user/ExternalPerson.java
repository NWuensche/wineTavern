package winetavern.model.user;

import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

/**
 * Entity for external persons
 * @author Niklas Wünsche
 */

@Entity
public class ExternalPerson {

    @Id @GeneratedValue private long id;

    private String name;
    private boolean isPayed;
    private Optional<Money> wage;

    public ExternalPerson(String name, boolean isPayed, Optional<Money> wage) {
        this.name = name;
        this.isPayed = isPayed;
        this.wage = wage;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    public Optional<Money> getWage() {
        return wage;
    }

    public void setWage(Optional<Money> wage) {
        this.wage = wage;
    }
    
}
