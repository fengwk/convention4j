package fun.fengwk.convention4j.api.page;

import java.io.Serializable;
import java.util.Objects;

/**
 * 游标分页查询器。
 *
 * @author fengwk
 */
public class CursorPageQuery<C> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前游标。
     */
    private final C cursor;

    /**
     * 游标分页限制。
     */
    private final int limit;

    public CursorPageQuery(C cursor, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be greater than or equal to 1");
        }
        this.cursor = cursor;
        this.limit = limit;
    }

    public C getCursor() {
        return cursor;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CursorPageQuery<?> that = (CursorPageQuery<?>) o;
        return limit == that.limit && Objects.equals(cursor, that.cursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursor, limit);
    }

    @Override
    public String toString() {
        return "CursorPageQuery{" +
            "cursor=" + cursor +
            ", limit=" + limit +
            '}';
    }

}
