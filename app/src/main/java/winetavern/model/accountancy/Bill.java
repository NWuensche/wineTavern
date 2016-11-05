package winetavern.model.accountancy;


import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.reservation.Table;

import javax.persistence.Entity;

import winetavern.model.reservation.Table;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */
@Entity
public class Bill extends AccountancyEntry {
    private Table table;
    //private Waitress waitress;

    @OneToMany(targetEntity=BillItem.class)
    private List<BillItem> billItemList;
}
