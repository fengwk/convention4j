package fun.fengwk.convention.api.page;

import java.io.Serializable;

/**
 * {@link Pageable}表示具有分页能力的对象。
 * 
 * @author fengwk
 */
public interface Pageable extends Serializable {

    /**
     * 获取页码，范围[1, +∞)。
     * 
     * @return
     */
    int getPageNumber();

    /**
     * 获取页面大小，范围[1, +∞)。
     * 
     * @return
     */
    int getPageSize();

    /**
     * 设置页码，范围[1, +∞)。
     * 
     * @return
     */
    void setPageNumber(int pageNumber);

    /**
     * 设置页面大小，范围[1, +∞)。
     * 
     * @return
     */
    void setPageSize(int pageSize);
    
}
