package winetavern.model.accountancy;

import org.salespointframework.accountancy.AccountancyEntry;
import winetavern.model.menu.MenuItem;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * Created by Michel on 11/3/2016.
 */
@Entity
public class BillItem {
    @Id
    private long id;

    @OneToOne(targetEntity = MenuItem.class)
    MenuItem menuItem;

    // maybe further expanding with "entrance cost" as billItem type or so
}
