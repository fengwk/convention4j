package fun.fengwk.convention4j.common.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class LitePageImpl<T> implements LitePage<T> {

    private static final long serialVersionUID = 1L;
    
    private final int pageNumber;
    private final int pageSize;
    private final List<T> results;
    private final boolean hasNext;

    /**
     *
     * @param pageNumber >= 1
     * @param pageSize >= 1
     * @param results not null
     * @param hasNext
     */
    public LitePageImpl(int pageNumber, int pageSize, List<T> results, boolean hasNext) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        if (results == null) {
            throw new NullPointerException("results cannot be null");
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.results = results;
        this.hasNext = hasNext;
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
    public boolean hasPrev() {
        return pageNumber > 1;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public <S> LitePage<S> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new LitePageImpl<>(pageNumber, pageSize, mappedResult, hasNext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LitePageImpl<?> litePage = (LitePageImpl<?>) o;
        return pageNumber == litePage.pageNumber && pageSize == litePage.pageSize && hasNext == litePage.hasNext
                && Objects.equals(results, litePage.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, results, hasNext);
    }

    @Override
    public String toString() {
        return "LitePageImpl{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", results=" + results +
                ", hasNext=" + hasNext +
                '}';
    }

}
