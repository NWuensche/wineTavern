package winetavern.splitter;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Niklas Wünsche
 */
public class Splitter<T> {

    private Stream<T> passed;
    private Stream<T> notPassed;

    protected Splitter(Stream<T> passed, Stream<T> notPassed) {
        this.passed = passed;
        this.notPassed = notPassed;
    }

    public Splitter<T> forEachPassed(Consumer<T> action) {
        passed.forEach(action);
        return this;
    }

    public Splitter<T> forEachNotPassed(Consumer<T> action) {
        notPassed.forEach(action);
        return this;
    }

}
