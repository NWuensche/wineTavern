package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import winetavern.model.reservation.Desk;
import winetavern.model.reservation.DeskRepository;

/**
 * Created by michel on 15.11.16.
 */
@RestController
public class DeskManager {
    @Autowired
    DeskRepository deskRepository;

    @RequestMapping("/desk/status")
    public String status(@RequestParam String deskName) {
        Desk desk = deskRepository.findByName(deskName);
        return deskName;
    }
}
