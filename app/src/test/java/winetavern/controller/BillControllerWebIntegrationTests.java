package winetavern.controller;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.AbstractWebIntegrationTests;
import winetavern.Helper;
import winetavern.model.accountancy.Bill;
import winetavern.model.accountancy.BillItem;
import winetavern.model.accountancy.BillItemRepository;
import winetavern.model.accountancy.BillRepository;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.user.EmployeeManager;
import winetavern.model.user.Roles;
import javax.transaction.Transactional;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.salespointframework.core.Currencies.EURO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Niklas Wünsche
 */
public class BillControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private BillRepository billRepository;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private BillItemRepository billItemRepository;
    @Autowired private EmployeeManager employeeManager;

    private BillItem fries;
    private Bill bill;

    @Before
    public void before() {
        dayMenuItemRepository.deleteAll();
        DayMenuItem dayMenuItem = new DayMenuItem("Pommes", "Description", Money.of(3, EURO), 3.0);
        dayMenuItemRepository.save(dayMenuItem);
        fries = new BillItem(dayMenuItem);
        billItemRepository.save(fries);
        bill = new Bill("Table 1", employeeManager.findByUsername("admin").get());
        bill.changeItem(fries, 5);
        billRepository.save(bill);
    }

    @Test
    public void serviceAndAdminAuthorized() throws Exception {
        RequestBuilder serviceRequest = post("/service/bills")
                .with(user("service").roles(Roles.SERVICE.getRealNameOfRole()));

        mvc.perform(serviceRequest)
                .andExpect(authenticated());

        RequestBuilder adminRequest = post("/service/bills")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(adminRequest)
                .andExpect(authenticated());
    }

    @Test
    public void showBillsModelAttributesRight() throws Exception{
        RequestBuilder request = post("/service/bills")
                .with(user("service").roles(Roles.SERVICE.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("active"))
                .andExpect(view().name("bills"));
    }

    @Test
    public void addBillRight() throws Exception {
        String desk = "Table 1";

        RequestBuilder request = post("/service/bills/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("table", desk);

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        Bill firstBill = billRepository.getFirst();

        assertThat(firstBill.getDesk(), is(desk));
    }

    @Test
    public void addBillItemRight() throws Exception {
        DayMenuItem dayMenuItem2 = new DayMenuItem("New Produtt", "Desc", Money.of(5, EURO), 3.5);
        dayMenuItemRepository.save(dayMenuItem2);

        RequestBuilder request = post("/service/bills/details/" + bill.getId() + "/add")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("itemid", "" + dayMenuItem2.getId())
                .param("quantity", "" + 4);

        mvc.perform(request)
                .andExpect(status().is3xxRedirection());

        assertThat(bill.getItems().size(), is(2));

        BillItem secondBillItem = bill.getItems()
                .stream()
                .collect(Collectors.toList())
                    .get(1);
        assertThat(secondBillItem.getQuantity(), is(4));
        assertThat(secondBillItem.getItem(), is(dayMenuItem2));
    }

    @Test
    public void printBillRight() throws Exception {
        assertThat(bill.isClosed(), is(false));

        RequestBuilder request = post("/service/bills/details/" + bill.getId() + "/print")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("bill"))
                .andExpect(view().name("printbill"));

        assertThat(bill.isClosed(), is(true));
    }


    @Test
    public void dontAddAnythingInDetails() throws Exception {
        DayMenuItem notAdded = new DayMenuItem("Not There", "Desc", Money.of(3, EURO), 3.0);
        dayMenuItemRepository.save(notAdded);

        RequestBuilder noQueryRequest = get("/service/bills/details/" + bill.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(noQueryRequest)
                .andExpect(model().attribute("bill", bill))
                .andExpect(model().attribute("menuitems", Arrays.asList(notAdded)))
                .andExpect(view().name("billdetails"));
    }

    @Test
    public void showDetailsRight() throws Exception {
        DayMenuItem dayMenuItem2 = new DayMenuItem("New Product", "Desc", Money.of(5, EURO), 3.5);
        dayMenuItemRepository.save(dayMenuItem2);
        BillItem billItem2 = new BillItem(dayMenuItem2);
        billItemRepository.save(billItem2);

        RequestBuilder queryRequest = get("/service/bills/details/" + bill.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("save", fries.getId() + ",7|" + billItem2.getId() + ",3|");

        mvc.perform(queryRequest)
                .andExpect(status().is3xxRedirection());

        assertThat(bill.getItems().size(), is(2));
        assertThat(bill.getItems().stream().findFirst().get().getQuantity(), is(7));
        assertThat(bill.getItems().contains(billItem2), is(true));
    }


    @Test
    public void dontSplitBill() throws Exception {
        RequestBuilder noSplitRequest = post("/service/bills/details/" + bill.getId() + "/split")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(noSplitRequest)
                .andExpect(model().attributeDoesNotExist("leftbill"))
                .andExpect(model().attributeExists("bill"))
                .andExpect(view().name("splitbill"));

        assertThat(billRepository.count(), is(1l));
    }

    @Test
    public void splitBillRight() throws Exception {
        DayMenuItem wineItem = new DayMenuItem("Wein", "Desc", Money.of(5, EURO), 3.5);
        dayMenuItemRepository.save(wineItem);
        BillItem wine = new BillItem(wineItem);
        billItemRepository.save(wine);

        bill.changeItem(wine, 4);

        billRepository.save(bill);

        RequestBuilder splitRequest = post("/service/bills/details/" + bill.getId() + "/split")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("query", fries.getId() + ",3|");

        mvc.perform(splitRequest)
                .andExpect(model().attributeExists("leftbill"))
                .andExpect(view().name("splitbill"));


        Bill secondBill = billRepository
                .stream()
                .collect(Collectors.toList())
                    .get(1);

        List<BillItem> secondBillItems = secondBill.getItems()
                .stream()
                .collect(Collectors.toList());

        BillItem wineOnSecondBill = secondBillItems.get(0);
        BillItem friesOnSecondBill = secondBillItems.get(1);

        assertThat(bill.getItems().size(), is(1));
        assertThat(fries.getQuantity(), is(3));
        assertThat(bill.getItems().contains(wine), is(false));

        assertThat(friesOnSecondBill.getItem(), is(fries.getItem()));
        assertThat(wineOnSecondBill.getQuantity(), is(4));
        assertThat(friesOnSecondBill.getQuantity(), is(2));
    }

}
