package winetavern.controller;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.AccountCredentials;
import winetavern.model.user.Person;
import winetavern.model.user.PersonManager;

/**
 * Controller, which maps {@link Person} related stuff
 * @author michel
 */

@Controller
public class PersonManagerController {

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private PersonManager personManager;

    @RequestMapping({"/admin/management/users", "/users"})
    public String addUsersMapper(Model model){
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("personManager", personManager);
        return "users";
    }

    @RequestMapping(value= "/admin/management/users/addNew", method=RequestMethod.POST)
    public String addUser(@ModelAttribute(value="accountcredentials") AccountCredentials registerCredentials) {
        UserAccount newAccount = userAccountManager.create(registerCredentials.getUsername(), registerCredentials.getPassword(), Role.of(registerCredentials.getRole()));
        newAccount.setFirstname(registerCredentials.getFirstName());
        newAccount.setLastname(registerCredentials.getLastName());

        userAccountManager.save(newAccount);

        Person newPerson = new Person(newAccount, registerCredentials.getAddress(), registerCredentials.getBirthday(), registerCredentials.getPersonTitle());
        personManager.save(newPerson);

        return "redirect:/users";
    }

    @RequestMapping(value= "/admin/management/users/changeUser/{pid}")
    public String changeUser(@PathVariable("pid") Long id, @ModelAttribute(value="accountcredentials") AccountCredentials changeCredentials) {
        Person changePerson = personManager.findOne(id).get();

        changePerson.getUserAccount().setLastname(changeCredentials.getLastName());
        deleteRole(changePerson);

        changePerson.getUserAccount().add(Role.of(changeCredentials.getRole()));

        changePerson.setAddress(changeCredentials.getAddress());

        userAccountManager.save(changePerson.getUserAccount());
        personManager.save(changePerson);


        return "redirect:/admin/management/users";
    }

    private Person deleteRole(Person person) {
        person.getUserAccount().remove(person.getRole());
        return person;
    }


}
