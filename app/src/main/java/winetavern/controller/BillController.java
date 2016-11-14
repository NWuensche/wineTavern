package winetavern.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sev
 */
@Controller
public class BillController {

    @RequestMapping("/bill")
    public String showBills(){
        return "bill";
    }
}
