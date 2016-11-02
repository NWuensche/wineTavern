/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kickstart.controller;


import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.salespointframework.useraccount.AuthenticationManager;
import kickstart.AccountCredentials;


@Controller
public class WelcomeController {
	@Autowired UserAccountManager manager;

    private AuthenticationManager authenticationManager;
    @Autowired
    public void WelcomeController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
	@RequestMapping("/")
	public String index(Model model) {
        if(this.authenticationManager.getCurrentUser().isPresent()) {
            AccountCredentials accountCredentials = new AccountCredentials();
            accountCredentials.setUsername(this.authenticationManager.getCurrentUser().get().getUsername());
            model.addAttribute("accountcredentials", accountCredentials);
            return "welcome";
        }
		return "login";

	}

	@RequestMapping("/users")
	public String users(Model model) {
		AccountCredentials registerCredentials = new AccountCredentials();
		model.addAttribute("accountcredentials", registerCredentials);
		return "users";
	}

	private void addAdminToDBIfNotThereYet() {
		String adminName = "admin";

		if(!isAdminInDB(adminName)) {
			UserAccount admin = manager.create(adminName, "asdf", Role.of("ADMIN"));
			manager.save(admin);
		}
	}

	private boolean isAdminInDB(String adminName) {
		return manager.findByUsername(adminName).isPresent();
	}

}
