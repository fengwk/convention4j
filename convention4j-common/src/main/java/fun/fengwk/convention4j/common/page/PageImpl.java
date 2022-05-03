package fun.fengwk.convention4j.common.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class PageImpl<T> implements Page<T> {

    private static final long serialVersionUID = 1L;
    
    private final int pageNumber;
    private final int pageSize;
    private final List<T> results;
    private final long totalCount;

    /**
     *
     * @param pageNumber >= 1
     * @param pageSize >= 1
     * @param results not null
     * @param totalCount >= 0
     */
    public PageImpl(int pageNumber, int pageSize, List<T> results, long totalCount) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        if (results == null) {
            throw new NullPointerException("results cannot be null");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount must be greater than or equal to 0");
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.results = Objects.requireNonNull(results);
        this.totalCount = totalCount;
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
        return pageNumber < getTotalPages();
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public long getTotalPages() {
        return totalCount % (long) pageSize == 0 ? totalCount / (long) pageSize : totalCount / (long) pageSize + 1;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public <S> Page<S> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new PageImpl<>(pageNumber, pageSize, mappedResult, totalCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageImpl<?> page = (PageImpl<?>) o;
        return pageNumber == page.pageNumber && pageSize == page.pageSize && totalCount == page.totalCount
                && Objects.equals(results, page.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, results, totalCount);
    }

    @Override
    public String toString() {
        return "PageImpl{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", results=" + results +
                ", totalCount=" + totalCount +
                '}';
    }

}
