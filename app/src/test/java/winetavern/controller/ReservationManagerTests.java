package winetavern.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Table;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Sev
 */
@Transactional
public class ReservationManagerTests {

    @Autowired
    ReservationRepository manager;

    @Test(expected = NullPointerException.class)
    public void throwWhenSaveNullReservation() {
        manager.save((Reservation) null);
    }

    @Test(expected = NullPointerException.class)
    public void throwWhenDeleteNullReservation() {
        manager.delete((Reservation) null);
    }

    @Test
    public void addReservationTest() {
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Reservation reservation = new Reservation("myName",new Table(10,1),time,duration);
        manager.save(reservation);
        assertThat(manager.findOne(reservation.getId()), is(reservation));
    }

    @Test
    public void findReservationsByTimeTest() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Table table = new Table(10,1);
        Reservation reservation = new Reservation("myName",table,time,duration);
        manager.save(reservation);
        //assertFalse(manager.findByTime(time).contains(reservation));
    }

}
