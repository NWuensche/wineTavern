package winetavern.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Desk;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Sev
 */
@Transactional
public class ReservationManagerTests extends AbstractWebIntegrationTests {

    @Autowired ReservationRepository manager;

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void throwWhenSaveNullReservation() {
        manager.save((Reservation) null);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void throwWhenDeleteNullReservation() {
        manager.delete((Reservation) null);
    }

    @Test
    public void addReservationTest() {
        TimeInterval interval = new TimeInterval(LocalDateTime.of(2016, 9, 11, 21, 30), LocalDateTime.of(2016, 9, 11,
                23,
                30));
        Reservation reservation = new Reservation("myName",4,new Desk("10",1),interval);
        manager.save(reservation);
        assertThat(manager.findOne(reservation.getId()).get(), is(reservation));
    }

    @Test
    public void findReservationsByTimeTest() throws Exception{
        TimeInterval interval = new TimeInterval(LocalDateTime.of(2016, 9, 11, 21, 30), LocalDateTime.of(2016, 9, 11,
                23,
                30));
        Desk desk = new Desk("10",1);
        Reservation reservation = new Reservation("myName",4, desk,interval);
        manager.save(reservation);
        Optional<Reservation> it = manager.findOne(reservation.getId());
        assertThat(it.get(), is(reservation));

    }

}
