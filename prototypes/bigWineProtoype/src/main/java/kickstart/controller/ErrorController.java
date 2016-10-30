package kickstart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by michel on 28.10.16.
 */
@Controller
public class ErrorController {
    @RequestMapping("/err0r")
    public String index() {
        return "welcome";
    }
}
