package winetavern.model.user;

import org.javamoney.moneta.Money;

/**
 * @author Niklas Wünsche
 */

public class Artist extends ExternalPerson {

    private boolean isPayed;
    private Money wage;

    public Artist(Money wage) {
        isPayed = true;
        this.wage = wage;
    }

    @Override
    public void pay() {
        isPayed = true;
    }

}
