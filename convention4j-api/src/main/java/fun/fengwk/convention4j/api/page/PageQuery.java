package fun.fengwk.convention4j.api.page;

import java.io.Serializable;
import java.util.Objects;

/**
 * 分页查询器。
 *
 * @author fengwk
 */
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private final int pageNumber;

    /**
     * 页面大小
     */
    private final int pageSize;

    public PageQuery(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }
        if (pageSize < 0) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 0");
        }
        if (pageSize > PageQueryConfig.getMaxPageSize()) {
            throw new IllegalArgumentException("pageSize must be less than or equal to "
                + PageQueryConfig.getMaxPageSize());
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageQuery pageQuery = (PageQuery) o;
        return pageNumber == pageQuery.pageNumber && pageSize == pageQuery.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize);
    }

    @Override
    public String toString() {
        return "PageQuery{" +
            "pageNumber=" + pageNumber +
            ", pageSize=" + pageSize +
            '}';
    }

}
