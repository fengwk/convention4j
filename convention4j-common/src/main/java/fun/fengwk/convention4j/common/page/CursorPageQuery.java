package fun.fengwk.convention4j.common.page;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 游标分页查询器。
 *
 * @author fengwk
 */
public class CursorPageQuery<C> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private C pageCursor;
    private int pageSize;

    /**
     *
     * @param pageCursor
     * @param pageSize >= 1
     */
    public CursorPageQuery(C pageCursor, int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }

        this.pageCursor = pageCursor;
        this.pageSize = pageSize;
    }

    /**
     * 获取当页游标，首个页面的游标为null，之后每个页面为上一页面最后元素的游标值。
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
     * 获取要查询的元素数量。
     * 
     * @return
     */
    public long getLimit() {
        return QueryNextSupport.getLimit(pageSize);
    }
    
    /**
     * 获取真实的结果集。
     * 
     * @param <E>
     * @param results
     * @return
     */
    public <E> List<E> getRealResults(List<E> results) {
        return QueryNextSupport.getRealResults(results, pageSize);
    }
    
    /**
     * 判断是否存在下一页。
     * 
     * @param resultsSize
     * @return
     */
    public boolean hasNext(int resultsSize) {
        return QueryNextSupport.hasNext(resultsSize, pageSize);
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
