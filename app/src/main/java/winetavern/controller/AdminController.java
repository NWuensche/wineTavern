package winetavern.controller;

import winetavern.AccountCredentials;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by nwuensche on 03.11.16.
 */
@Controller
public class AdminController {

    @Autowired UserAccountManager manager;

    @RequestMapping("/admin/users")
    public String addUsersMapper(Model model){
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("staffCollection", manager.findEnabled());
        return "users";
    }

}
