package winetavern.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.DeskRepository;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Desk;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Sev, Michel Kunkler
 */
@Transactional
public class ReservationManagerWebIntegrationTests extends AbstractWebIntegrationTests {

    @Autowired ReservationRepository reservationRepository;
    @Autowired ReservationManager reservationManager;
    @Autowired DeskRepository deskRepository;

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
     * @throws Exception
     */
    @Test
    public void tableSetReserved() throws Exception{
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
     * @throws Exception
     */
    @Test
    public void tableSetNotReserved() throws Exception{
        Reservation reservation = createDeskReserved();
        Desk desk = reservation.getDesk();


        List<Desk> reservedDesks = reservationManager.getReservatedTablesByTime(
                LocalDateTime.of(2015, 9, 11, 22, 0)
        );
        assertThat(reservedDesks.contains(desk), is(false));
    }
}
