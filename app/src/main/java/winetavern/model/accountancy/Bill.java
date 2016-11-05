package winetavern.model.accountancy;


import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.reservation.Table;

import javax.persistence.*;

import winetavern.model.reservation.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */
@Entity
public class Bill extends AccountancyEntry {
    @ManyToOne(targetEntity = Table.class)
    private Table table;
    //private Waitress waitress;

    @OneToMany(targetEntity=BillItem.class)
    private List<BillItem> billItemList;
}
