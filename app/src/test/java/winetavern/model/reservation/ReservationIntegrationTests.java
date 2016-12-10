package winetavern.model.reservation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;
import winetavern.model.management.TimeInterval;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Niklas Wünsche
 */
public class ReservationIntegrationTests extends AbstractIntegrationTests{

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private DeskRepository deskRepository;

    private Reservation reservation;
    private Reservation reservation2;
    private Desk desk;
    private Desk desk2;
    private TimeInterval interval;
    private TimeInterval interval2;


    @Before
    public void before() {
        deskRepository.deleteAll();
        reservationRepository.deleteAll();

        LocalDateTime start = LocalDateTime.of(2016, 11, 23, 20, 40);
        interval = new TimeInterval(start, start.plusHours(3));
        interval2 = new TimeInterval(start, start.plusHours(4));

        desk = new Desk("Tisch 1", 4);
        deskRepository.save(desk);
        desk2 = new Desk("Tisch 2", 5);
        deskRepository.save(desk2);

        reservation = new Reservation("Gast 1", 3, desk, interval);
        reservationRepository.save(reservation);
        reservation2 = new Reservation("Arnold", 4, desk2, interval2);
        reservationRepository.save(reservation2);
    }

    @Test
    public void addReservationCorrect() {
        Reservation reservation2 = new Reservation("Peter Mueller", 3, desk2, interval);
        reservationRepository.save(reservation2);
        desk2.addReservation(reservation2);

        List<Reservation> deskList = reservationRepository.findByDesk(desk2);
        assertThat(deskList.contains(reservation2), is(true));
    }

    @Test
    public void oldDeskDeleted() {
        Desk desk2 = new Desk("Tisch 2", 4);
        deskRepository.save(desk2);
        reservation.setDesk(desk2);
        assertThat(reservationRepository.findByDesk(desk2).isEmpty(), is(false));
        assertThat(reservationRepository.findByDesk(desk).isEmpty(), is(true));
    }

    @Test
    public void findByDeskWorks() {
        assertThat(reservationRepository.findByDesk(desk).contains(reservation), is(true));
    }

    @Test
    public void findAllByOrderByGuestNameWorks() {
        assertArrayEquals(reservationRepository.findAllByOrderByGuestName().toArray(), new Reservation[]{reservation2, reservation});
    }

    @Test
    public void findByCapacityWorks() {
        Desk bigDesk = new Desk("Tisch 3", 6);

        deskRepository.save(bigDesk);

        List<Desk> foundDesk = deskRepository.findByCapacityGreaterThanEqualOrderByCapacity(5);
        assertArrayEquals(foundDesk.toArray(), new Desk[]{desk2, bigDesk});
    }

    @Test
    public void findByNameWorks() {
        Desk desk4 = new Desk("Tisch 4", 4);
        deskRepository.save(desk4);

        assertThat(deskRepository.findByName("Tisch 4"), is(desk4));
    }

}
