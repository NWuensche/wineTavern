package winetavern;

import org.salespointframework.core.SalespointRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Niklas Wünsche
 */
@NoRepositoryBean
public interface ExtraRepository<T, I extends Serializable> extends SalespointRepository<T, I> {

    default List<T> convertToList() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    default T getFirst() {
        return stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    default Stream<T> stream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

}
