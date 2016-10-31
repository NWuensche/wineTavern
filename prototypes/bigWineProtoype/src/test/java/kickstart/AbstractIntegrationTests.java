package kickstart;

/**
 * Created by nwuensche on 31.10.16.
 */

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class, which connects tests to Application
 *
 * @author Niklas Wünsche
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class AbstractIntegrationTests {
}
