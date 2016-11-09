package winetavern.model.accountancy;

import winetavern.model.menu.DayMenuItem;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by Michel on 11/3/2016.
 */

@Entity
public class BillItem {
    @Id private long id;

    @OneToOne(targetEntity = DayMenuItem.class)
    DayMenuItem dayMenuItem;

    // maybe further expanding with "entrance cost" as billItem type or so
}
