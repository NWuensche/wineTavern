package winetavern.model.user;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractIntegrationTests;
import winetavern.model.DateParameter;

/**
 * Test class for {@link Person}
 */

@Transactional
public class PersonTests extends AbstractIntegrationTests{

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private PersonManager personManager;

    private Person person;
    private String address;
    private UserAccount acc;
    DateParameter birthday;

    @Before
    public void setUp() {
        acc = userAccountManager.create("testAccount", "1234", Role.of(Roles.SERVICE.getNameOfRoleWithPrefix()));
        address = "Neuer Weg 3, 04912 Berlin";
        birthday = new DateParameter();
        birthday.setYear(1980);
        birthday.setMonth(12);
        birthday.setDay(30);
    }

    @Test
    public void newPersonInDB() {
        person = new Person(acc, address, birthday);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).isPresent(), is(true));
    }

    @Test
    public void displayedRoleOfPersonIsRight() {
        person = new Person(acc, address, birthday);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getDisplayNameOfRole(), is("Bedienung"));
    }

    @Test
    public void newPersonInDBWithoutAddress() {
        person = new Person(acc, null, birthday);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getAddress().isPresent(), is((false)));
    }

    @Test
    public void newPersonInDBWithoutBirthday() {
        person = new Person(acc, address, null);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getBirthday().isPresent(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenPersonHas2Roles() {
        acc = userAccountManager.create("testAccount", "1234", Roles.SERVICE.getRole(), Roles.ACCOUNTANT.getRole());
        person = new Person(acc, address, birthday);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenPersonHas0Roles() {
        acc = userAccountManager.create("testAccount", "1234");
        person = new Person(acc, address, birthday);
    }

}
