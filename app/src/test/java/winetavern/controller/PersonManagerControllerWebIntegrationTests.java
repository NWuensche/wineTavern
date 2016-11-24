package winetavern.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import winetavern.AbstractWebIntegrationTests;
import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.springframework.test.web.servlet.RequestBuilder;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;
import winetavern.model.user.Roles;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Test class für {@link PersonManagerController}
 * @author Niklas Wünsche
 */

@Transactional
public class PersonManagerControllerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired PersonManager personManager;
    @Autowired UserAccountManager userAccountManager;

    private String userName;
    private String password;

    @Before
    public void before() {
        userName = "userName";
        password = "1234";
    }

    @Test
    public void redirectToUsers() throws Exception {
        mvc.perform(get("/admin/management/users").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole())))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }

    private RequestBuilder createRequestBuilder(String userName, String password) {
        RequestBuilder request = post("/admin/management/users/addNew").with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("personTitle", "Herr")
                .param("firstName", "Hans")
                .param("lastName", "Müller")
                .param("birthday", "1990/12/12")
                .param("username", userName)
                .param("password", password)
                .param("role", Roles.COOK.getNameOfRoleWithPrefix())
                .param("address", "Mein Haus");
        return request;
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoName() throws Exception {
        RequestBuilder request = createRequestBuilder(null, password);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenNoPassword() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, null);

        mvc.perform(request);
    }

    @Test(expected = NestedServletException.class)
    public void throwWhenTwoUsersWithSameName() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, password);

        mvc.perform(request);
        mvc.perform(request);
    }

    @Test
    public void savedNewPerson() throws Exception {
        RequestBuilder request = createRequestBuilder(userName, password);
        mvc.perform(request);

        Optional<UserAccount> user = userAccountManager.findByUsername(userName);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getRoles().stream().findFirst().get(), is(Role.of("ROLE_COOK")));
        assertThat(user.get().getUsername(), is("userName"));
        assertThat(user.get().getFirstname(), is("Hans"));
        assertThat(user.get().getLastname(), is("Müller"));

        Optional<Person> person = personManager.findByUserAccount(user.get());
        assertThat(person.isPresent(), is(true));
        assertThat(person.get().getBirthday().get(), is(LocalDate.of(1990,12,12)));
        assertThat(person.get().getAddress().get(), is("Mein Haus"));
        assertThat(person.get().getDisplayNameOfRole(), is("Koch"));
        assertThat(person.get().getPersonTitle(), is("Herr"));
    }

    @Test
    public void changeNewPerson() throws Exception{
        saveNewPerson();
        String personId = getPersonId();

        mvc.perform(get("/admin/management/users/changeUser/" + personId).with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("firstName", "DontSave")
                .param("lastName", "Schwab")
                .param("address", "Best House")
                .param("role", Roles.SERVICE.getNameOfRoleWithPrefix()));

        Person changedPerson = personManager.findByUserAccount(userAccountManager.findByUsername(userName).get()).get();

        assertThat(changedPerson.getUserAccount().getFirstname(), is("Hans"));
        assertThat(changedPerson.getUserAccount().getLastname(), is("Schwab"));
        assertThat(changedPerson.getAddress().get(), is("Best House"));
        assertThat(changedPerson.getUserAccount().getRoles().stream().findFirst().get(), is(Roles.SERVICE.getRole()));

    }

    private void saveNewPerson() throws Exception{
        RequestBuilder request = createRequestBuilder(userName, password);
        mvc.perform(request);
    }

    private String getPersonId() {
        return personManager.findByUserAccount(userAccountManager.findByUsername(userName).get()).get().getId().toString();
    }

}
