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

@Transactional
public class BillControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired private BillRepository billRepository;
    @Autowired private DayMenuItemRepository dayMenuItemRepository;
    @Autowired private BillItemRepository billItemRepository;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private UserAccountManager userAccountManager;

    private BillItem billItem;
    private Bill bill;

    @Before
    public void before() {
        DayMenuItem dayMenuItem = new DayMenuItem("Product", "Description", Money.of(3, EURO), 3.0);
        dayMenuItemRepository.save(dayMenuItem);
        billItem = new BillItem(dayMenuItem);
        billItemRepository.save(billItem);
        bill = new Bill("Table 1",
                employeeManager.findByUserAccount(userAccountManager.findByUsername("admin").get()).get());

        bill.changeItem(billItem, 5);
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

        Bill firstBill = Helper.getFirstItem(billRepository.findAll());

        assertThat(firstBill.getDesk(), is(desk));
    }

    @Test
    public void showDetailsRight() throws Exception {
        DayMenuItem dayMenuItem2 = new DayMenuItem("New Procut", "Desc", Money.of(5, EURO), 3.5);
        dayMenuItemRepository.save(dayMenuItem2);
        BillItem billItem2 = new BillItem(dayMenuItem2);
        billItemRepository.save(billItem2);

        RequestBuilder noQueryRequest = get("/service/bills/details/" + bill.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(noQueryRequest)
                .andExpect(model().attributeExists("menuitems"))
                .andExpect(view().name("billdetails"));

        RequestBuilder queryRequest = get("/service/bills/details/" + bill.getId())
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("save", billItem.getId() + ",7|" + billItem2.getId() + ",3|");

        mvc.perform(queryRequest)
                .andExpect(status().is3xxRedirection());

        assertThat(bill.getItems().size(), is(2));
        assertThat(Helper.getFirstItem(bill.getItems()).getQuantity(), is(7));
        assertThat(bill.getItems().contains(billItem2), is(true));

    }

    @Test
    public void splitBillRight() throws Exception {
        RequestBuilder noSplitRequest = post("/service/bills/details/" + bill.getId() + "/split")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(noSplitRequest)
                .andExpect(model().attributeDoesNotExist("leftbill"))
                .andExpect(model().attributeExists("bill"))
                .andExpect(view().name("splitbill"));

        RequestBuilder splitRequest = post("/service/bills/details/" + bill.getId() + "/split")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("query", billItem.getId() + ",3|");

        mvc.perform(splitRequest)
                .andExpect(model().attributeExists("leftbill"))
                .andExpect(view().name("splitbill"));


        Bill secondBill = Helper.convertToList(billRepository.findAll()).get(1);
        BillItem secondBillItem = Helper.getFirstItem(secondBill.getItems());

        assertThat(Helper.getFirstItem(bill.getItems()).getQuantity(), is(3));

        assertThat(secondBillItem.getItem(), is(billItem.getItem()));
        assertThat(secondBillItem.getQuantity(), is(2));
    }

}
