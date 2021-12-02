package fun.fengwk.convention.api.page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author fengwk
 */
public class LitePageImpl<T> implements LitePage<T> {

    private static final long serialVersionUID = 1L;
    
    private int pageNumber;
    private int pageSize;
    private List<T> results;
    private boolean hasNext;
    
    public LitePageImpl(int pageNumber, int pageSize, List<T> results, boolean hasNext) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("PageNumber must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("PageSize must be greater than or equal to 1");
        }
        
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.results = Objects.requireNonNull(results);
        this.hasNext = hasNext;
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
        return hasNext;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public <S> LitePage<S> map(Function<? super T, ? extends S> mapper) {
        List<S> mappedResult = results.stream().map(mapper).collect(Collectors.toList());
        return new LitePageImpl<>(pageNumber, pageSize, mappedResult, hasNext);
    }

}
