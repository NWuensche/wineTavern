package kickstart.controller;

import kickstart.AccountCredentials;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by nwuensche on 03.11.16.
 */
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired UserAccountManager manager;

    @RequestMapping("/users")
    public String addUsersMapper(Model model){
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("staffCollection", manager.findEnabled());
        return "users";
    }

}
