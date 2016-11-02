package kickstart.controller;

import kickstart.RegisterCredentials;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller to store a new User in the DB.
 * @author michel
 */

@Controller
@PreAuthorize("isAuthenticated()")
public class UserAccountManagementController {

    UserAccountManager userAccountManager;

    @Autowired
    public UserAccountManagementController(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }

    @RequestMapping(value="/addNew", method=RequestMethod.POST)
    public String index(@ModelAttribute(value="registercredentials") RegisterCredentials registerCredentials) {
        UserAccount account = userAccountManager.create(registerCredentials.getName(), registerCredentials.getPassword());

        userAccountManager.save(account);

        return "users";
    }

    public UserAccountManager getUserAccountManager(){
        return userAccountManager;
    }

}
