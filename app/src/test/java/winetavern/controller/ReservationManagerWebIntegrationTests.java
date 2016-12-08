package winetavern.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Desk;
import winetavern.model.user.Roles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Sev, Michel Kunkler
 */
@Transactional
public class ReservationManagerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired ReservationRepository reservationRepository;
    @Autowired ReservationManager reservationManager;
    @Autowired DeskRepository deskRepository;

    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;
    private Reservation reservation4;


    @Before
    public void before() {
        Desk desk1 = new Desk("Table 1", 4);
        Desk desk2 = new Desk("Table 2", 5);
        deskRepository.save(Arrays.asList(desk1, desk2));

        LocalDateTime thisYear = LocalDateTime.of(2016, 1, 1, 1, 30);
        TimeInterval first = new TimeInterval(thisYear, thisYear.plusHours(3));
        TimeInterval second = new TimeInterval(thisYear.plusYears(1), thisYear.plusYears(1).plusHours(3));
        TimeInterval third = new TimeInterval(thisYear.plusYears(2), thisYear.plusYears(2).plusHours(3));

        reservation1 = new Reservation("Guest 1", 4, desk1, first);
        reservation2 = new Reservation("Guest 2", 4, desk1, second);
        reservation3 = new Reservation("Guest 3", 3, desk2, third);
        reservation4 = new Reservation("Guest 4", 5, desk2, second);
        reservationRepository.save(Arrays.asList(reservation1, reservation2, reservation3, reservation4));
    }



    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void throwWhenSaveNullReservation() {
        reservationRepository.save((Reservation) null);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void throwWhenDeleteNullReservation() {
        reservationRepository.delete((Reservation) null);
    }

    private Reservation createDeskReserved() {
        Desk desk = new Desk("T1", 1);

        TimeInterval interval = new TimeInterval(
                LocalDateTime.of(2016, 9, 11, 21, 30),
                LocalDateTime.of(2016, 9, 11, 23, 30)
        );
        Reservation reservation = new Reservation("Hugo Boss", 4, desk, interval);
        reservationRepository.save(reservation);
        return reservation;
    }

    /**
     * Creates a new table (desk), adds a reservation to this table and tests if the reservation is findable by its id.
     */
    @Test
    public void addReservationTest() {
        Reservation reservation = createDeskReserved();
        assertThat(reservationRepository.findOne(reservation.getId()).get(), is(reservation));
    }

    /**
     * Creates a new table (desk), adds a reservation to this table and tests if the table is set as reservated
     * by querying a time in between the reservation interval.
     */
    @Test
    public void tableSetReserved() {
        Reservation reservation = createDeskReserved();
        Desk desk = reservation.getDesk();


        List<Desk> reservedDesks = reservationManager.getReservatedTablesByTime(
                LocalDateTime.of(2016, 9, 11, 22, 0)
        );
        assertThat(reservedDesks.contains(desk), is(true));
    }

    /**
     * Creates a new table (desk), adds a reservation to this table and tests if the table is NOT set as reservated
     * by querying a time NOT in between the reservation interval.
     */
    @Test
    public void tableSetNotReserved() {
        Reservation reservation = createDeskReserved();
        Desk desk = reservation.getDesk();


        List<Desk> reservedDesks = reservationManager.getReservatedTablesByTime(
                LocalDateTime.of(2015, 9, 11, 22, 0)
        );
        assertThat(reservedDesks.contains(desk), is(false));
    }

    @Test
    public void deskToNameRight() {
        Desk desk1 = new Desk("Table 1", 4);
        Desk desk2 = new Desk("Table 2", 4);

        List<String> names = reservationManager.deskToName(Arrays.asList(desk1, desk2));

        assertThat(names, is(Arrays.asList("Table 1", "Table 2")));
    }

    @Test
    public void isActiveRight() {
        Desk desk = new Desk("Table 1", 4);
        TimeInterval timeInterval = new TimeInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        Reservation reservation = new Reservation("Guest 1", 3, desk, timeInterval);

        assertThat(reservationManager.isActive(reservation, LocalDateTime.now().plusHours(1)), is(true));
        assertThat(reservationManager.isActive(reservation, LocalDateTime.now().minusHours(1)), is(true));
        assertThat(reservationManager.isActive(reservation, LocalDateTime.now().minusHours(3)), is(false));
    }

    @Test
    public void reservationTimeValidatorRightIfNoParam() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()));

        mvc.perform(request)
                .andExpect(model().attributeExists("reservationTableList"))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void reservationTimeValidatorRightIfDesk() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("desk", "Table 1");


        mvc.perform(request)
                .andExpect(model().attributeExists("reservationTableList"))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(model().attributeExists("deskReservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void sortReservationTimeValidatorByDateRightIfReservationTime() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11")
                .param("sort", "date");


        mvc.perform(request)
                .andExpect(model().attribute("reservationTableList", Arrays.asList(reservation2, reservation4, reservation3)))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void sortReservationTimeValidatorByNameRightIfReservationTime() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11")
                .param("sort", "name");

        mvc.perform(request)
                .andExpect(model().attribute("reservationTableList", Arrays.asList(reservation2, reservation3, reservation4)))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void sortReservationTimeValidatorByPersonsRightIfReservationTime() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11")
                .param("sort", "persons");

        mvc.perform(request)
                .andExpect(model().attribute("reservationTableList", Arrays.asList(reservation3, reservation2, reservation4)))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void ReservationTimeValidatorRightIfReservationTime() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11");

        mvc.perform(request)
                .andExpect(model().attribute("reservationTableList", Arrays.asList(reservation2, reservation3, reservation4)))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

    @Test
    public void sortReservationTimeValidatorByTrashRightIfReservationTime() throws Exception {
        RequestBuilder request = post("/service/reservation")
                .with(user("admin").roles(Roles.ADMIN.getRealNameOfRole()))
                .param("reservationtime", "2016/11/11 11:11")
                .param("sort", "trash"); // Is a non-existing parameter


        mvc.perform(request)
                .andExpect(model().attribute("reservationTableList", Arrays.asList(reservation2, reservation3, reservation4)))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(view().name("reservation"));
    }

}
