package winetavern.controller;

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
import winetavern.model.user.Employee;
import winetavern.model.user.EmployeeManager;

/**
 * Controller, which maps {@link Employee} related stuff
 * @author michel
 */

@Controller
public class EmployeeManagerController {

    @Autowired private UserAccountManager userAccountManager;
    @Autowired private EmployeeManager employeeManager;
    @Autowired private AuthenticationManager authManager;

    @RequestMapping("/admin/management/users")
    public String usersSite(Model model){
        AccountCredentials registerCredentials = new AccountCredentials();
        model.addAttribute("accountcredentials", registerCredentials);
        model.addAttribute("employeeManager", employeeManager);
        model.addAttribute("currUserAccount", authManager.getCurrentUser().get());
        return "users";
    }

    @RequestMapping("/admin/management/users/add")
    public String addEmployee(@ModelAttribute(value="accountcredentials") AccountCredentials registerCredentials,
                              Model model) {
        if(userNameInDB(registerCredentials.getUsername())) {
            model.addAttribute("usernameTaken", true);
            return usersSite(model);
        }

        saveNewEmployee(registerCredentials);

        return "redirect:/admin/management/users";
    }

    private boolean userNameInDB(String username) {
        return userAccountManager.findByUsername(username).isPresent();
    }

    private void saveNewEmployee(AccountCredentials registerCredentials) {
        UserAccount newAccount = userAccountManager.create(registerCredentials.getUsername(),
                registerCredentials.getPassword(), Role.of(registerCredentials.getRole()));
        newAccount.setFirstname(registerCredentials.getFirstName());
        newAccount.setLastname(registerCredentials.getLastName());

        userAccountManager.save(newAccount);

        String[] birthday = registerCredentials.getBirthday().split("\\.");

        Employee newEmployee = new Employee(newAccount,
                registerCredentials.getAddress(),
                birthday[2] + "/" + birthday[1] + "/" + birthday[0],
                registerCredentials.getPersonTitle());
        employeeManager.save(newEmployee);
    }

    @RequestMapping("/admin/management/users/edit/{pid}")
    public String editEmployee(@PathVariable("pid") Long id,
                       @ModelAttribute(value="accountcredentials") AccountCredentials changeCredentials) {
        Employee changeEmployee = employeeManager.findOne(id).get();

        changeEmployee.getUserAccount().setLastname(changeCredentials.getLastName());
        deleteRole(changeEmployee);

        changeEmployee.getUserAccount().add(Role.of(changeCredentials.getRole()));

        changeEmployee.setAddress(changeCredentials.getAddress());

        userAccountManager.save(changeEmployee.getUserAccount());
        employeeManager.save(changeEmployee);

        return "redirect:/admin/management/users";
    }

    private Employee deleteRole(Employee employee) {
        employee.getUserAccount().remove(employee.getRole());
        return employee;
    }

    @RequestMapping("/admin/management/users/disable/{pid}")
    public String disableEmployee(@PathVariable("pid") Long id) {
        Employee disableEmployee = employeeManager.findOne(id).get();
        disableEmployee.getUserAccount().setEnabled(false);
        userAccountManager.save(disableEmployee.getUserAccount());
        employeeManager.save(disableEmployee);

        return "redirect:/admin/management/users";
    }

}
