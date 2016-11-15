package winetavern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import winetavern.model.reservation.Table;
import winetavern.model.reservation.TableRepository;

/**
 * Created by michel on 15.11.16.
 */
@RestController
public class TableManager {
    @Autowired
    TableRepository tableRepository;

    @RequestMapping("/table/status")
    public String status(@RequestParam String tableName) {
        Table table = tableRepository.findByName(tableName);
        return tableName;
    }
}
