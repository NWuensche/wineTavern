package winetavern.controller;

import org.apache.tomcat.jni.Time;
import org.junit.Test;
import org.salespointframework.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import winetavern.AbstractWebIntegrationTests;
import winetavern.model.management.TimeInterval;
import winetavern.model.reservation.Reservation;
import winetavern.model.reservation.ReservationRepository;
import winetavern.model.reservation.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
        Reservation reservation = new Reservation("myName",new Table(10,1),interval);
        manager.save(reservation);
        assertThat(manager.findOne(reservation.getId()).get(), is(reservation));
    }

    @Test
    public void findReservationsByTimeTest() throws Exception{
        TimeInterval interval = new TimeInterval(LocalDateTime.of(2016, 9, 11, 21, 30), LocalDateTime.of(2016, 9, 11,
                23,
                30));
        Table table = new Table(10,1);
        Reservation reservation = new Reservation("myName",table,interval);
        manager.save(reservation);
        Optional<Reservation> it = manager.findOne(reservation.getId());
        assertThat(it.get(), is(reservation));

    }

}
