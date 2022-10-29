package fun.fengwk.convention4j.common.page;

import java.io.Serializable;
import java.util.List;
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

        this.pageSize = pageSize;
    }
    
    /**
     * 获取要查询的元素数量，数量是{@link #getPageSize()}加1，目的是为了从查询结果集中获知是否有下一页。
     * 
     * @return
     */
    public long getLimit() {
        return DiscoverNextPageSupport.getLimit(pageSize);
    }

    /**
     * 获取真实的结果集。
     *
     * @param results 查询到的结果集。
     * @return
     * @param <E> 结果集元素类型。
     */
    public <E> List<E> getRealResults(List<E> results) {
        return DiscoverNextPageSupport.getRealResults(results, pageSize);
    }
    
    /**
     * 判断是否存在下一页。
     * 
     * @param resultsSize 结果集大小。
     * @return true-存在下一页，false-不存在下一页。
     */
    public boolean hasNext(int resultsSize) {
        return DiscoverNextPageSupport.hasNext(resultsSize, pageSize);
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
