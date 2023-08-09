package fun.fengwk.convention4j.api.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class DefaultCursorPage<T, C> implements CursorPage<T, C> {

    private static final long serialVersionUID = 1L;

    private final C cursor;
    private final int limit;
    private final List<T> results;
    private final C nextCursor;
    private final boolean more;

    public DefaultCursorPage(C cursor, int limit, List<T> results, C nextCursor, boolean more) {
        this.cursor = cursor;
        this.limit = limit;
        this.results = results;
        this.nextCursor = nextCursor;
        this.more = more;
    }

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
    public boolean isMore() {
        return more;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new DefaultCursorPage<>(cursor, limit, mappedResult, nextCursor, more);
    }

    @Override
    public <S> CursorPage<S, C> mapAll(Function<? super List<T>, ? extends List<S>> mapper) {
        List<S> mappedResult = mapper.apply(results);
        return new DefaultCursorPage<>(cursor, limit, mappedResult, nextCursor, more);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultCursorPage<?, ?> that = (DefaultCursorPage<?, ?>) o;
        return limit == that.limit && more == that.more && Objects.equals(cursor, that.cursor) && Objects.equals(results, that.results) && Objects.equals(nextCursor, that.nextCursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursor, limit, results, nextCursor, more);
    }

    @Override
    public String toString() {
        return "DefaultCursorPage{" +
            "cursor=" + cursor +
            ", limit=" + limit +
            ", results=" + results +
            ", nextCursor=" + nextCursor +
            ", more=" + more +
            '}';
    }

}
