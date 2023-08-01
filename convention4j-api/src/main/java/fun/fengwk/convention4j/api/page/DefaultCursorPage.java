package fun.fengwk.convention4j.api.page;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DefaultCursorPage<T, C> implements CursorPage<T, C> {

    private static final long serialVersionUID = 1L;

    private final C cursor;
    private final int limit;
    private final List<T> results;
    private final C nextCursor;
    private final boolean hasNext;

    @Override
    public C getCursor() {
        return cursor;
    }

    @Override
    public int getLimit() {
        return limit;
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
        return new DefaultCursorPage<>(cursor, limit, mappedResult, nextCursor, hasNext);
    }

    @Override
    public <S> CursorPage<S, C> mapAll(Function<? super List<T>, ? extends List<S>> mapper) {
        List<S> mappedResult = mapper.apply(results);
        return new DefaultCursorPage<>(cursor, limit, mappedResult, nextCursor, hasNext);
    }

}
