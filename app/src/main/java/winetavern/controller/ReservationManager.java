package winetavern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sev
 */
@Controller
public class ReservationManager {

    @RequestMapping("/reservation")
    public String newReservation(){
        return "reservation";
    }
}
