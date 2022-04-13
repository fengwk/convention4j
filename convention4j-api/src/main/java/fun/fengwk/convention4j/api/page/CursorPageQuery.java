package fun.fengwk.convention4j.api.page;

import java.util.List;

/**
 * 
 * @author fengwk
 */
public class CursorPageQuery<C> implements CursorPageable<C> {

    private static final long serialVersionUID = 1L;
    
    private C pageCursor;
    private int pageSize;
    
    public CursorPageQuery(C pageCursor, int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        
        this.pageCursor = pageCursor;
        this.pageSize = pageSize;
    }

    @Override
    public C getPageCursor() {
        return pageCursor;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageCursor(C pageCursor) {
        this.pageCursor = pageCursor;
    }

    @Override
    public void setPageSize(int pageSize) {
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

}
