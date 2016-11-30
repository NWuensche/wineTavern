package winetavern.model.reservation;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.model.management.TimeInterval;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Niklas Wünsche
 */

@Transactional
public class ReservationRepositoryTests extends AbstractIntegrationTests {

    @Autowired ReservationRepository reservationRepository;
    @Autowired DeskRepository deskRepository;

    private Desk desk1;
    private Desk desk2;
    private TimeInterval interval1;
    private TimeInterval interval2;
    private Reservation reservation1;
    private Reservation reservation2;

    @Before
    public void before() {
        deskRepository.deleteAll();
        reservationRepository.deleteAll();

        LocalDateTime start = LocalDateTime.of(2016, 11, 23, 20, 40);

        desk1 = new Desk("Tisch 1", 5);
        desk2 = new Desk("Tisch 2", 3);
        deskRepository.save(Arrays.asList(desk1, desk2));

        interval1 = new TimeInterval(start, start.plusHours(3));
        interval2 = new TimeInterval(start, start.plusHours(4));

        reservation1 = new Reservation("Bernd", 3, desk1, interval1);
        reservation2 = new Reservation("Arnold", 4, desk2, interval2);
        reservationRepository.save(Arrays.asList(reservation1, reservation2));
    }

    @Test
    public void findByDeskWorks() {
        assertThat(reservationRepository.findByDesk(desk1).contains(reservation1), is(true));
    }

    @Test
    public void findAllByOrderByGuestNameWorks() {
        assertArrayEquals(reservationRepository.findAllByOrderByGuestName().toArray(), new Reservation[]{reservation2, reservation1});
    }

}
