package winetavern.model.accountancy;

import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

/**
 * @author Louis
 */

public class Bill extends Order {
    int table;

    public Bill(UserAccount userAccount, int table) {
        super(userAccount);
        this.table = table;
    }

    public Bill(UserAccount userAccount, PaymentMethod paymentMethod, int table) {
        super(userAccount, paymentMethod);
        this.table = table;
    }

    public int getTable() {
        return table;
    }
}
