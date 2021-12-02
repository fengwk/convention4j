package fun.fengwk.convention.api.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class PageImpl<T> implements Page<T> {

    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int pageSize;
    private List<T> results;
    private int totalCount;
    
    public PageImpl(int pageNumber, int pageSize, List<T> results, int totalCount) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("PageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("PageSize must be greater than or equal to 1");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("TotalCount must be greater than or equal to 0");
        }
        
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.results = Objects.requireNonNull(results);
        this.totalCount = totalCount;
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
    public boolean hasPrev() {
        return pageNumber > 1;
    }

    @Override
    public boolean hasNext() {
        return pageNumber < getTotalPages();
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public int getTotalPages() {
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public <S> Page<S> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new PageImpl<>(pageNumber, pageSize, mappedResult, totalCount);
    }

}
