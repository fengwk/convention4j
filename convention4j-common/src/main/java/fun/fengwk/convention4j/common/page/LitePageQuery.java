package fun.fengwk.convention4j.common.page;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link LitePageQuery}是专为{@link LitePage}的查询而设计的，通过在查询时多查一个元素得到hasNext信息。
 * 
 * @author fengwk
 */
public class LitePageQuery implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int pageSize;

    /**
     *
     * @param pageNumber >= 1
     * @param pageSize >= 1
     */
    public LitePageQuery(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        int maxPageSize = PageQueryLimiter.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new IllegalArgumentException("pageSize must be less than or equal to " + maxPageSize);
        }
        
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * 获取页码，范围[1, +∞)。
     *
     * @return
     */
    public int getPageNumber() {
        return pageNumber;
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
     * 设置页码，范围[1, +∞)。
     *
     * @param pageNumber >= 1
     */
    public void setPageNumber(int pageNumber) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }

        this.pageNumber = pageNumber;
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
     * 获取要跳过的元素数量。
     * 
     * @return
     */
    public long getOffset() {
        return ((long) pageNumber - 1L) * (long) pageSize;
    }

    /**
     * 获取要查询的元素数量。
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
        LitePageQuery that = (LitePageQuery) o;
        return pageNumber == that.pageNumber && pageSize == that.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize);
    }

    @Override
    public String toString() {
        return "LitePageQuery{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }

}
