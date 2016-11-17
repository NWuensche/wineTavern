package winetavern.model.accountancy;


import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.reservation.Desk;

import javax.persistence.*;

import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */

@Entity
public class Bill extends AccountancyEntry {
    @ManyToOne(targetEntity = Desk.class)
    private Desk desk;

    @OneToMany(targetEntity=BillItem.class)
    private List<BillItem> billItemList;
}
