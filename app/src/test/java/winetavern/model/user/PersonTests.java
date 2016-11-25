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

import java.time.LocalDate;
import java.util.ArrayList;

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
    private String birthday;
    private String personTitle = PersonTitle.MISTER.getGerman();

    @Before
    public void setUp() {
        acc = userAccountManager.create("testAccount", "1234", Roles.SERVICE.getRole());
        address = "Neuer Weg 3, 04912 Berlin";
        birthday = "1980/12/30";
    }

    @Test
    public void newPersonInDB() {
        person = new Person(acc, address, birthday, personTitle);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).isPresent(), is(true));
        Person savedPerson = personManager.findOne(person.getId()).get();
        assertThat(savedPerson.getBirthday().get(), is(LocalDate.of(1980, 12, 30)));
        assertThat(savedPerson.getPersonTitle(), is(PersonTitle.MISTER.getGerman()));
        assertThat(savedPerson.getUserAccount(), is(acc));
        assertThat(savedPerson.getRole(), is(Roles.SERVICE.getRole()));
    }

        @Test
    public void displayedRoleOfPersonIsRight() {
        person = new Person(acc, address, birthday, personTitle);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getDisplayNameOfRole(), is("Bedienung"));
    }

    @Test
    public void newPersonInDBWithoutAddress() {
        person = new Person(acc, null, birthday, personTitle);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getAddress().isPresent(), is((false)));
    }

    @Test
    public void newPersonInDBWithoutBirthday() {
        person = new Person(acc, address, null, personTitle);
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getBirthday().isPresent(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenPersonHas2Roles() {
        acc = userAccountManager.create("testAccount2", "1234", Roles.SERVICE.getRole(), Roles.ACCOUNTANT.getRole());
        person = new Person(acc, address, birthday, personTitle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenPersonHas0Roles() {
        acc = userAccountManager.create("testAccount2", "1234");
        person = new Person(acc, address, birthday, personTitle);
    }

    @Test
    public void isNotEnabled() {
        person = new Person(acc, address, null, personTitle);
        personManager.save(person);
        acc.setEnabled(false);
        assertThat(person.isEnabled(), is(false));
    }

    @Test
    public void isEnabled() {
        person = new Person(acc, address, null, personTitle);
        personManager.save(person);
        acc.setEnabled(true);
        assertThat(person.isEnabled(), is(true));
    }

    @Test
    public void setAddressRight() {
        person = new Person(acc, address, null, personTitle);
        String newAddress = "New Address";
        person.setAddress(newAddress);
        assertThat(person.getAddress().get(), is(newAddress));
    }

    @Test
    public void findEnabled() {
        disableAdminFromInitalizer();

        savePersons();
        disableOnePerson();

        ArrayList<Person> enabled = personManager.findEnabled();

        Person person3 = personManager.findByUserAccount(userAccountManager.findByUsername("testAccount3").get()).get();
        assertArrayEquals(enabled.toArray(), new Person[]{person, person3});
    }

    private void disableAdminFromInitalizer() {
        Person disableAdmin = personManager.findByUserAccount(userAccountManager.findByUsername("admin").get()).get();
        disableAdmin.getUserAccount().setEnabled(false);
    }

    private void savePersons() {
        UserAccount acc2 = userAccountManager.create("testAccount2", "1234", Roles.SERVICE.getRole());
        UserAccount acc3 = userAccountManager.create("testAccount3", "1234", Roles.SERVICE.getRole());
        person = new Person(acc, address, birthday, personTitle);
        Person person2 = new Person(acc2, address, birthday, personTitle);
        Person person3 = new Person(acc3, address, birthday, personTitle);
        personManager.save(person);
        personManager.save(person2);
        personManager.save(person3);
    }

    private void disableOnePerson() {
        Person disablePerson2 = personManager.findByUserAccount(userAccountManager.findByUsername("testAccount2").get()).get();
        disablePerson2.getUserAccount().setEnabled(false);
    }

}
