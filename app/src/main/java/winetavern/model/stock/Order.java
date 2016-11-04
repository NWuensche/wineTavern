package winetavern.model.stock;

import org.salespointframework.quantity.Quantity;
import winetavern.model.user.Person;

/**
 * @author Louis
 */
public class Order {
    private Quantity quantity;
    private Product product;
    private Person responsiblePerson;
    private OrderState state;

    public Order(Quantity quantity, Product product, Person responsiblyPerson) {
        this.quantity = quantity;
        this.product = product;
        this.responsiblePerson = responsiblyPerson;
        this.state = OrderState.OPEN;
    }

    public Person getResponsiblePerson() {
        return responsiblePerson;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public OrderState getState() {
        return state;
    }

    public void order() {
        if (state != OrderState.OPEN)
            throw new IllegalStateException("Order must be in state OPEN when getting ordered");
        this.state = OrderState.ORDERED;
    }

    public void receive() {
        if (state != OrderState.ORDERED)
            throw new IllegalStateException("Order must be in state ORDERED when getting received");
        this.state = OrderState.RECEIVED;
        product.setAmount(product.getAmount().add(quantity));
    }

    public void cancel() {
        if (state == OrderState.RECEIVED)
            throw new IllegalStateException("A received order can not be cancelled");
        this.state = OrderState.CANCELLED;
    }

    protected enum OrderState {
        OPEN,
        ORDERED,
        RECEIVED,
        CANCELLED
    }
}
