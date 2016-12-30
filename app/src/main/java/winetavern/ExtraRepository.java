package winetavern;

import org.salespointframework.core.SalespointRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    default T getFirstItem() {
        return convertToList().get(0);
    }

}
