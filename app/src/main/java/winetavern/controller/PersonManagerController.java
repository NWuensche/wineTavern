package winetavern.controller;

import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Role;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import winetavern.AccountCredentials;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @Autowired private AuthenticationManager authManager;

    @RequestMapping("/admin/management/users")
    public String addUsersMapper(Model model){
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("personManager", personManager);
        model.addAttribute("currUserAccount", authManager.getCurrentUser().get());
        return "users";
    }

    @RequestMapping("/admin/management/users/add")
    public String addPerson(@ModelAttribute(value="accountcredentials") AccountCredentials registerCredentials) {
        UserAccount newAccount = userAccountManager.create(registerCredentials.getUsername(),
                registerCredentials.getPassword(), Role.of(registerCredentials.getRole()));
        newAccount.setFirstname(registerCredentials.getFirstName());
        newAccount.setLastname(registerCredentials.getLastName());

        userAccountManager.save(newAccount);

        Person newPerson = new Person(newAccount, registerCredentials.getAddress(),
                registerCredentials.getBirthday(), registerCredentials.getPersonTitle());
        personManager.save(newPerson);

        return "redirect:/admin/management/users";
    }

    @RequestMapping("/admin/management/users/edit/{pid}")
    public String edit(@PathVariable("pid") Long id,
                       @ModelAttribute(value="accountcredentials") AccountCredentials changeCredentials) {
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

    @RequestMapping("/admin/management/users/disable/{pid}")
    public String disablePerson(@PathVariable("pid") Long id) {
        Person disablePerson = personManager.findOne(id).get();
        disablePerson.getUserAccount().setEnabled(false);
        userAccountManager.save(disablePerson.getUserAccount());
        personManager.save(disablePerson);

        return "redirect:/admin/management/users";
    }

}
