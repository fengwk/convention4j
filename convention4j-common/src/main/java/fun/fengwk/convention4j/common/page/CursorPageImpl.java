package fun.fengwk.convention4j.common.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class CursorPageImpl<T, C> implements CursorPage<T, C> {

    private static final long serialVersionUID = 1L;
    
    private final C pageCursor;
    private final int pageSize;
    private final List<T> results;
    private final C nextCursor;
    private final boolean hasNext;

    /**
     *
     * @param pageCursor
     * @param pageSize >= 1
     * @param results not null
     * @param nextCursor
     * @param hasNext
     */
    public CursorPageImpl(C pageCursor, int pageSize, List<T> results, C nextCursor, boolean hasNext) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        if (results == null) {
            throw new NullPointerException("results cannot be null");
        }

        this.pageCursor = pageCursor;
        this.pageSize = pageSize;
        this.results = results;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    @Override
    public C getPageCursor() {
        return pageCursor;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public C getNextCursor() {
        return nextCursor;
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
    public <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new CursorPageImpl<>(pageCursor, pageSize, mappedResult, nextCursor, hasNext);
    }

    @Override
    public <S> CursorPage<S, C> mapAll(Function<? super List<T>, ? extends List<S>> mapper) {
        List<S> mappedResult = mapper.apply(results);
        return new CursorPageImpl<>(pageCursor, pageSize, mappedResult, nextCursor, hasNext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CursorPageImpl<?, ?> that = (CursorPageImpl<?, ?>) o;
        return pageSize == that.pageSize && hasNext == that.hasNext && Objects.equals(pageCursor, that.pageCursor)
                && Objects.equals(results, that.results) && Objects.equals(nextCursor, that.nextCursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageCursor, pageSize, results, nextCursor, hasNext);
    }

    @Override
    public String toString() {
        return "CursorPageImpl{" +
                "pageCursor=" + pageCursor +
                ", pageSize=" + pageSize +
                ", results=" + results +
                ", nextCursor=" + nextCursor +
                ", hasNext=" + hasNext +
                '}';
    }

}
