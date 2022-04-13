package fun.fengwk.convention4j.api.page;

import java.util.List;

/**
 * {@link LitePageQuery}是专为{@link LitePage}的查询而设计的，通过在查询时多查一个元素得到hasNext信息。
 * 
 * @author fengwk
 */
public class LitePageQuery implements Pageable {
    
    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int pageSize;
    
    public LitePageQuery(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than or equal to 1");
        }
        
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取要跳过的元素数量。
     * 
     * @return
     */
    public long getOffset() {
        return (pageNumber - 1) * pageSize;
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
