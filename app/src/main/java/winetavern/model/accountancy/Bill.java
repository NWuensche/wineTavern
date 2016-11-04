package winetavern.model.accountancy;

import winetavern.model.reservation.Table;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 11/3/2016.
 */
public class Bill {
    @Id
    private long id;

    private Date closeTime;
    private Table table;
    //private Waitress waitress;

    @OneToMany(targetEntity=BillItem.class)
    private List<BillItem> billItemList;
}
