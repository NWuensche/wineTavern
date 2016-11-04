package winetavern.model.stock;

import winetavern.model.user.Person;

/**
 * @author Louis
 */
public class Order {
    private int quantity;
    private Product product;
    private Person responsiblePerson;
    private OrderState state;

    public Order(int quantity, Product product, Person responsiblyPerson) {
        this.quantity = quantity;
        this.product = product;
        this.responsiblePerson = responsiblyPerson;
        this.state = OrderState.OPEN;
    }

    public Person getResponsiblePerson() {
        return responsiblePerson;
    }

    public void ordered() {
        if (state != OrderState.OPEN)
            throw new IllegalStateException("Order must be in state OPEN when getting ordered");
        this.state = OrderState.ORDERED;
    }

    public void received() {
        if (state != OrderState.ORDERED)
            throw new IllegalStateException("Order must be in state ORDERED when getting received");
        this.state = OrderState.RECEIVED;
    }

    private enum OrderState {
        OPEN,
        ORDERED,
        RECEIVED
    }
}
