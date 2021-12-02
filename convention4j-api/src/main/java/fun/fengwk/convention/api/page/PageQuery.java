package fun.fengwk.convention.api.page;

/**
 * 
 * @author fengwk
 */
public class PageQuery implements Pageable {

    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int pageSize;
    
    public PageQuery(int pageNumber, int pageSize) {
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
        return pageSize;
    }

}
