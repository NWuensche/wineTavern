package winetavern;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class, which connects tests to {@link Application}.
 *
 * @author Niklas Wünsche
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public abstract class AbstractIntegrationTests {
}
