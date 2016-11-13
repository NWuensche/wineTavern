package winetavern.controller;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Louis
 */

@ControllerAdvice
public class GlobalAttributesController {
    private final BusinessTime businessTime;

    @Autowired
    public GlobalAttributesController(BusinessTime businessTime) {
        this.businessTime = businessTime;
    }

    @ModelAttribute
    public void setBusinessTime(Model model) {
        model.addAttribute("businessTime", businessTime.getTime().format(DateTimeFormatter.
                ofLocalizedDateTime(FormatStyle.SHORT)));
        model.addAttribute("minutes",businessTime.getTime().getMinute());
        model.addAttribute("hours",businessTime.getTime().getHour());
    }
}
