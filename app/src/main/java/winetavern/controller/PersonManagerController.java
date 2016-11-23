package winetavern.controller;

import org.salespointframework.inventory.InventoryItem;
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

    @RequestMapping("/admin/management/users/details/{pid}")
    public String detail(@PathVariable("pid") String id, Model model) {
        Long idLong = Long.parseLong(id);
        model.addAttribute("currPerson", personManager.findOne(idLong).get());
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("personManager", personManager);
        return "users";
    }

}
