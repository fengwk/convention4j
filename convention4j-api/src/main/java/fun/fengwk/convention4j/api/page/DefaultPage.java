package fun.fengwk.convention4j.api.page;

import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
@Data
public class DefaultPage<T> implements Page<T> {

    private static final long serialVersionUID = 1L;

    private final int pageNumber;
    private final int pageSize;
    private final List<T> results;
    private final long totalCount;

    @Override
    public boolean hasPrev() {
        return pageNumber > 1;
    }

    @Override
    public boolean hasNext() {
        return pageNumber < getTotalPages();
    }

    @Override
    public long getTotalPages() {
        return totalCount % (long) pageSize == 0 ? totalCount / (long) pageSize : totalCount / (long) pageSize + 1;
    }

    @Override
    public <S> Page<S> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new DefaultPage<>(pageNumber, pageSize, mappedResult, totalCount);
    }

    @Override
    public <S> Page<S> mapAll(Function<? super List<T>, ? extends List<S>> mapper) {
        List<S> mappedResult = mapper.apply(results);
        return new DefaultPage<>(pageNumber, pageSize, mappedResult, totalCount);
    }

}
