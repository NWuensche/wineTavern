package winetavern.splitter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Niklas Wünsche
 */
public class SplitterTests {
    List<String> words;
    SplitBuilder<String> splitBuilder;

    @Before
    public void before() {
        words = Arrays.asList("a", "A", "b", "B", "c", "C");
        splitBuilder = new SplitBuilder<>(words);
    }

    @Test
    public void splitRight() {
        List<String> lowerCase = new ArrayList<>();
        List<String> upperCase = new ArrayList<>();

        Splitter<String> splitter = splitBuilder.splitBy(word -> word.equals(word.toLowerCase()));
        splitter.forEachPassed(lowerCase::add)
                .forEachNotPassed(upperCase::add);

        assertThat(lowerCase, is(Arrays.asList("a", "b", "c")));
        assertThat(upperCase, is(Arrays.asList("A", "B", "C")));
    }

}
