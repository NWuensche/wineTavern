package winetavern.model.user;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
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

    @Before
    public void setUp() {
        UserAccount acc = userAccountManager.create("testAccount", "1234");
        Address address = new Address("Straße", "3a", "02374", "Berlin");
        DateParameter birthday = new DateParameter();
        birthday.setYear(1980);
        birthday.setMonth(12);
        birthday.setDay(30);
        person = new Person(acc, address, birthday);
    }

    @Test
    public void newPersonInDB() {
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).isPresent(), is(true));
    }

    @Test
    public void newPersonInDBWithoutAddress() {
        person.setAddress(new Address(null, null, null, null));
        personManager.save(person);
        assertThat(personManager.findOne(person.getId()).get().getAddress().toString(), is(""));
    }


}
