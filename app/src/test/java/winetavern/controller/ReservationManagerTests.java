package winetavern.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.Table;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Sev
 */
@Transactional
public class ReservationManagerTests {

    @Autowired ReservationManager manager;

    @Test(expected = NullPointerException.class)
    public void addNullTest() throws Exception{
        manager.addReservation(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeNullTest() throws Exception{
        manager.addReservation(null);
    }

    @Test
    public void addReservationTest() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Reservation reservation = new Reservation(1,"myName",new Table(10,1),time,duration);
        manager.addReservation(reservation);
        assertThat(manager.getReservationById(reservation.getId()),is(reservation));
    }

    @Test
    public void getFreeTablesByTimeTest() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofHours(2);
        Table table = new Table(10,1);
        Reservation reservation = new Reservation(1,"myName",table,time,duration);
        manager.addReservation(reservation);
        assertFalse(manager.getFreeTablesByTime(time).contains(table));
    }

}
