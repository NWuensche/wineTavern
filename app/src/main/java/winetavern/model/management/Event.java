package winetavern.model.management;

import org.javamoney.moneta.Money;

import java.time.LocalDateTime;

/**
 * @author Louis
 */
public class Event {
    private LocalDateTime date;
    private String name;
    private String description;
    private Money price;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}
