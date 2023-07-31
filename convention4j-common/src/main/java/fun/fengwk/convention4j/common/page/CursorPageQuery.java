package fun.fengwk.convention4j.common.page;

import java.io.Serializable;
import java.util.Objects;

/**
 * 游标分页器。
 *
 * @author fengwk
 */
public class CursorPageQuery<C> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private C pageCursor;
    private int pageSize;

    /**
     * 构建游标分页器。
     *
     * @param pageCursor 当页游标，如果是首页则为null或一个哨兵值（推荐使用null）。
     * @param pageSize >= 1，页面大小。
     */
    public CursorPageQuery(C pageCursor, int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        int maxPageSize = PageQueryLimiter.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new IllegalArgumentException("pageSize must be less than or equal to " + maxPageSize);
        }

        this.pageCursor = pageCursor;
        this.pageSize = pageSize;
    }

    /**
     * 获取当页游标，如果是首页则为null或一个哨兵值（推荐使用null）。
     *
     * @return
     */
    public C getPageCursor() {
        return pageCursor;
    }

    /**
     * 获取页面大小，范围[1, +∞)。
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置当页游标。
     *
     * @param pageCursor
     */
    public void setPageCursor(C pageCursor) {
        this.pageCursor = pageCursor;
    }

    /**
     * 设置页面大小，范围[1, +∞)。
     *
     * @param pageSize >= 1
     */
    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        int maxPageSize = PageQueryLimiter.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new IllegalArgumentException("pageSize must be less than or equal to " + maxPageSize);
        }

        this.pageSize = pageSize;
    }

    /**
     * 获取要查询的元素数量，数量是{@link #getPageSize()}加1，目的是为了从查询结果集中获知是否有下一页。
     *
     * @return
     */
    public int getLimit() {
        return Pages.getLimit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CursorPageQuery<?> that = (CursorPageQuery<?>) o;
        return pageSize == that.pageSize && Objects.equals(pageCursor, that.pageCursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageCursor, pageSize);
    }

    @Override
    public String toString() {
        return "CursorPageQuery{" +
                "pageCursor=" + pageCursor +
                ", pageSize=" + pageSize +
                '}';
    }

}
