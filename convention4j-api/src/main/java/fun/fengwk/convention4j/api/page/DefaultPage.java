package fun.fengwk.convention4j.api.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class DefaultPage<T> implements Page<T> {

    private static final long serialVersionUID = 1L;

    private final int pageNumber;
    private final int pageSize;
    private final List<T> results;
    private final long totalCount;

    public DefaultPage(int pageNumber, int pageSize, List<T> results, long totalCount) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.results = results;
        this.totalCount = totalCount;
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

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPage<?> that = (DefaultPage<?>) o;
        return pageNumber == that.pageNumber && pageSize == that.pageSize && totalCount == that.totalCount && Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, results, totalCount);
    }

    @Override
    public String toString() {
        return "DefaultPage{" +
            "pageNumber=" + pageNumber +
            ", pageSize=" + pageSize +
            ", results=" + results +
            ", totalCount=" + totalCount +
            '}';
    }

}
