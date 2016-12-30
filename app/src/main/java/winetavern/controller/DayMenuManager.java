package winetavern.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import winetavern.Helper;
import winetavern.model.management.Event;
import winetavern.model.management.EventCatalog;
import winetavern.model.menu.DayMenu;
import winetavern.model.menu.DayMenuItem;
import winetavern.model.menu.DayMenuItemRepository;
import winetavern.model.menu.DayMenuRepository;
import winetavern.model.user.Vintner;
import winetavern.model.user.VintnerManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by Michel on 11/4/2016.
 * @author Michel Kunkler
 */

@Controller
public class DayMenuManager {

    private DayMenuRepository dayMenuRepository;
    private DayMenuItemRepository dayMenuItemRepository;
    private final Inventory<InventoryItem> stock;
    private BusinessTime businessTime;
    private EventCatalog eventCatalog;
    private VintnerManager vintnerManager;

    @Autowired
    public DayMenuManager(Inventory<InventoryItem> stock, DayMenuRepository dayMenuRepository,
                          DayMenuItemRepository dayMenuItemRepository,
                          BusinessTime businessTime, EventCatalog eventCatalog,
                          VintnerManager vitnerManager) {
        this.businessTime = businessTime;
        this.stock = stock;
        this.dayMenuRepository = dayMenuRepository;
        this.dayMenuItemRepository = dayMenuItemRepository;
        this.eventCatalog = eventCatalog;
        this.vintnerManager = vitnerManager;
    }

    @RequestMapping("/admin/menu/show")
    public ModelAndView showMenus(ModelAndView modelAndView) {
        Iterable<DayMenu> dayMenuList = dayMenuRepository.findAll();
        modelAndView.addObject("menus", dayMenuList);
        modelAndView.setViewName("daymenulist");
        return modelAndView;
    }

    @RequestMapping("/admin/menu/add")
    public String addMenu() {
        return "addmenu";
    }

    @RequestMapping(value = "/admin/menu/add", method = RequestMethod.POST)
    public String addMenuPost(@ModelAttribute("date") String date) {
        LocalDate creationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDateTime creationDateTime = LocalDateTime.of(creationDate, LocalTime.MIN);
        DayMenu dayMenu = copyPreDayMenu(creationDate);

        if(dayMenu == null) {
            dayMenu = new DayMenu(creationDate);
        }

        //check if daymenu already exists
        if(dayMenuRepository.findByDay(dayMenu.getDay()) == null) {

            //check for vintner day
            Vintner vintner = null;
            for( Event event : eventCatalog.findAll() ) {
                if(event.getInterval().timeInInterval(creationDateTime) && event.isVintnerDay()) {
                    vintner = (Vintner)event.getPerson();
                    break;
                }
            }
            if(vintner != null) {
                // set vintnerday products
                dayMenu = new DayMenu();
                dayMenu.setDay(creationDate);
                for( DayMenuItem dayMenuItem : dayMenuItemRepository.findAll() )  {
                    if(!dayMenuItem.isEnabled())
                        continue;
                    for( Product wine : vintner.getWineSet()) {
                       if(wine == dayMenuItem.getProduct()) {
                           DayMenuItem discountedDayMenuItem = new DayMenuItem();
                           discountedDayMenuItem.setQuantityPerProduct(dayMenuItem.getQuantityPerProduct());
                           discountedDayMenuItem.setDescription(dayMenuItem.getDescription());
                           discountedDayMenuItem.setName(dayMenuItem.getName());
                           discountedDayMenuItem.setProduct(dayMenuItem.getProduct());
                           discountedDayMenuItem.setPrice(dayMenuItem.getPrice().divide(2.0));
                           dayMenu.addMenuItem(discountedDayMenuItem);
                           discountedDayMenuItem.setEnabled(false);
                           dayMenuItemRepository.save(discountedDayMenuItem);
                           dayMenu.addMenuItem(discountedDayMenuItem);
                       }
                    }
                }
                dayMenuRepository.save(dayMenu);
            } else {
                // set preday menu
                dayMenuRepository.save(dayMenu);
            }
            return "redirect:/admin/menu/edit/" + dayMenu.getId();
        } else {
            Long existingId = dayMenuRepository.findByDay(dayMenu.getDay()).getId();
            return "redirect:/admin/menu/edit/" + existingId.toString();
        }
    }

    @RequestMapping(value = "/admin/menu/remove", method = RequestMethod.POST)
    public String removeMenu(@RequestParam("daymenuid") Long dayMenuId) {
        DayMenu dayMenu = dayMenuRepository.findOne(dayMenuId).get();
        if(dayMenu != null) {
            dayMenu.getDayMenuItems().forEach((dayMenuItem) ->
            {
                dayMenu.removeMenuItem(dayMenuItem);
            });
            dayMenuRepository.delete(dayMenu);
        }
        return "redirect:/admin/menu/show";
    }

    @RequestMapping("/admin/menu/edit/{pid}")
    public String editMenu(@PathVariable("pid") Long id, Model model) {
        DayMenu dayMenu = dayMenuRepository.findOne(id).get();
        model.addAttribute("daymenu", dayMenu);
        model.addAttribute("stock", stock);
        model.addAttribute("frommenuitemid", id);
        return "editdaymenu";
    }


    protected DayMenu copyPreDayMenu(LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        DayMenu preDayMenu = dayMenuRepository.findByDay(yesterday);
        if (preDayMenu == null)
            return null;
        DayMenu newDayMenu = new DayMenu(today); //preDayMenu.clone(dayMenuItemRepository);
        dayMenuRepository.save(newDayMenu);
        for(DayMenuItem dayMenuItem : preDayMenu.getDayMenuItems()) {
            newDayMenu.addMenuItem(dayMenuItem);
        }
        return newDayMenu;
    }

    /**
     * prints all daymenu items in a pdf file.
     * @param id the daymenu id
     */
    @RequestMapping("/admin/menu/print/{pid}")
    public String printMenu(@PathVariable("pid") Long id) {
        DayMenu dayMenu = dayMenuRepository.findOne(id).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Document document = new Document();
        Font boldFont = new Font();
        boldFont.setStyle(Font.BOLD);
        Font catFont = new Font();
        catFont.setStyle(Font.BOLD);
        catFont.setSize(18);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream
                    ("src\\main\\resources\\daymenu\\daymenu.pdf"));
            document.open();

            //setting pdf attributes
            document.addAuthor("Zur Frölichen Reblaus");
            document.addCreator("WinetavernSystem");
            document.addTitle("Tageskarte " + dayMenu.getDay().format(formatter));

            Paragraph title = new Paragraph("Zur Fröhlichen Reblaus\n\n", catFont); //title
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Map<String, List<DayMenuItem>> sortedItems = new HashMap<>();

            for (DayMenuItem item : dayMenu.getDayMenuItems()) { //map every item to its category
                String category = item.getProduct().getCategories()
                        .stream()
                        .findFirst()
                            .orElse("");
                if (!sortedItems.containsKey(category))
                    sortedItems.put(category, new LinkedList<>());

                sortedItems.get(category).add(item);
            }

            for (String category : sortedItems.keySet()) { //for each category
                List<DayMenuItem> itemList = sortedItems.get(category);
                itemList.sort(Comparator.comparing(DayMenuItem::getName)); //sort items alphabetically

                PdfPTable menuItems = new PdfPTable(2); //invisible table for daymenuItem|price
                menuItems.setWidthPercentage(80);

                for (DayMenuItem item : itemList) { //for each item in this category
                    PdfPCell cellName = new PdfPCell(new Paragraph(item.getName()));
                    cellName.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellName.setBorderWidth(0);
                    PdfPCell cellPrice = new PdfPCell(new Paragraph(Helper.moneyToEuroString(item.getPrice())));
                    cellPrice.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellPrice.setBorderWidth(0);
                    menuItems.addCell(cellName);
                    menuItems.addCell(cellPrice);
                }

                if (itemList.size() > 0) { //only print category if it contains items
                    Paragraph categoryTitle = new Paragraph("\n" + category, boldFont);
                    categoryTitle.setSpacingAfter(5);
                    categoryTitle.setIndentationLeft(40);
                    document.add(categoryTitle); //add the category title
                }
                document.add(menuItems);
            }

            document.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return "daymenupdf";
    }

}