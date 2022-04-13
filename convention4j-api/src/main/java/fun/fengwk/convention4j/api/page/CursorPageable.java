package fun.fengwk.convention4j.api.page;

import java.io.Serializable;

/**
 * {@link CursorPageable}表示具有游标分页能力的对象。
 * 
 * @author fengwk
 */
public interface CursorPageable<C> extends Serializable {

    /**
     * 获取当页游标，首个页面的游标为null，之后每个页面为上一页面最后元素的游标值。
     * 
     * @return
     */
    C getPageCursor();

    /**
     * 获取页面大小，范围[1, +∞)。
     * 
     * @return
     */
    int getPageSize();

    /**
     * 设置当页游标。
     * 
     * @return
     */
    void setPageCursor(C pageCursor);

    /**
     * 设置页面大小，范围[1, +∞)。
     * 
     * @return
     */
    void setPageSize(int pageSize);
    
}
