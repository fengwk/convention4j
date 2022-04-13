package fun.fengwk.convention4j.api.page;

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
    
    private C pageCursor;
    private int pageSize;
    private List<T> results;
    private C nextCursor;
    private boolean hasNext;
    
    public CursorPageImpl(C pageCursor, int pageSize, List<T> results, C nextCursor, boolean hasNext) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("PageSize must be greater than or equal to 1");
        }
        
        this.pageCursor = pageCursor;
        this.pageSize = pageSize;
        this.results = Objects.requireNonNull(results);
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
        return new CursorPageImpl<S, C>(pageCursor, pageSize, mappedResult, nextCursor, hasNext);
    }

}
