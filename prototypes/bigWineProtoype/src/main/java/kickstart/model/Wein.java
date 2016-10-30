package kickstart.model;

import javax.persistence.*;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michel on 30.10.16.
 */
@Entity
public class Wein extends Product{


    public Wein(String name, Money price) {
        super(name, price);
    }
}
