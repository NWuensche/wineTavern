package winetavern.model.reservation;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import winetavern.AbstractIntegrationTests;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nwuensche on 29.11.16.
 */

@Transactional
public class DeskRepositoryTests extends AbstractIntegrationTests {

    @Autowired private DeskRepository deskRepository;

    private Desk desk;
    private Desk desk2;
    private Desk desk3;

    @Before
    public void before() {
        deskRepository.deleteAll();

        Desk desk = new Desk("Tisch 1", 4);
        Desk desk2 = new Desk("Tisch 2", 5);
        Desk desk3 = new Desk("Tisch 3", 6);

        deskRepository.save(Arrays.asList(desk, desk2, desk3));
    }

    @Test
    public void findByCapacityWorks() {
        List<Desk> foundDeskdeskRepository = deskRepository.findByCapacityGreaterThanEqualOrderByCapacity(5);
        assertArrayEquals(foundDeskdeskRepository.toArray(), new Desk[]{desk2, desk3});
    }

    @Test
    public void findByNameWorks() {
        Desk desk4 = new Desk("Tisch 4", 4);
        deskRepository.save(desk4);

        assertThat(deskRepository.findByName("Tisch 4"), is(desk4));
    }

}
