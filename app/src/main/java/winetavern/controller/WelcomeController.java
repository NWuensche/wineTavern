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
package winetavern.controller;

import lombok.NonNull;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import winetavern.Helper;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.management.TimeInterval;
import winetavern.model.user.VintnerManager;
import winetavern.splitter.SplitBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;




import java.util.stream.Collectors;


@Controller
public class WelcomeController {
	@Autowired UserAccountManager manager;
	@Autowired EventController eventController;
	@Autowired EventCatalog eventCatalog;
	@Autowired VintnerManager vintnerManager;
    @NonNull @Autowired EventCatalog events;
    @NonNull @Autowired BusinessTime time;

    private AuthenticationManager authenticationManager;

    @Autowired
    public void WelcomeController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

	@RequestMapping("/")
	public String index(Model model) {
        if(authenticationManager.getCurrentUser().isPresent()){
            return "redirect:/dashboard";
        } else {
            model.addAttribute("events",news());

            return "index";
        }

	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }


    private List<String> news(){
        TimeInterval interval = new TimeInterval(time.getTime(),time.getTime().plusMonths(2));
        eventController.checkVintnerDays();

        List<String> res = eventController.getAllEvents(vintnerManager,eventCatalog)
                .stream()
                .filter(event -> interval.intersects(event.getInterval()))
                .sorted(Comparator.comparing(event -> event.getInterval().getStart()))
                .map(event -> {
                    LocalDateTime start = event.getInterval().getStart();
                    LocalDateTime end = event.getInterval().getEnd();

                    return  "<h2>" + Helper.localDateTimeToDateString(start)  +
                            "</h2><b>" + event
                            .getName() +
                            "</b> " +
                            "<i>" +
                            getTimeIfEventIsNotFullDay(start, end) +
                             "</i><br/>" + event.getDescription() + "<hr/>";
                })
                .collect(Collectors.toList());

        return res;
    }

    private String getTimeIfEventIsNotFullDay(LocalDateTime start, LocalDateTime end) {
        return !start.equals(end) ? Helper.localDateTimeToTimeString(start) + " - " +
                Helper.localDateTimeToTimeString(end) : "ganztägig";
    }

}
