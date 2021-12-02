package fun.fengwk.convention.api.page;

import java.util.List;
import java.util.function.Function;

/**
 * {@link Pages}提供了一系列工厂方法以便创建各类分页结果。
 * 
 * @author fengwk
 */
public class Pages {
    
    private Pages() {}
    
    /**
     * 创建{@link Page}。
     * 
     * @param <E>
     * @param pageable
     * @param results
     * @param totalCount
     * @return
     */
    public static <E> Page<E> page(Pageable pageable, List<E> results, int totalCount) {
        return new PageImpl<>(pageable.getPageNumber(), pageable.getPageSize(), results, totalCount);
    }
    
    /**
     * 创建{@link LitePage}。
     * 
     * @param <E>
     * @param pageable
     * @param results
     * @param hasNext
     * @return
     */
    public static <E> LitePage<E> litePage(Pageable pageable, List<E> results, boolean hasNext) {
        return new LitePageImpl<>(pageable.getPageNumber(), pageable.getPageSize(), results, hasNext);
    }
    
    /**
     * 创建{@link LitePage}。
     * 
     * @param <E>
     * @param litePageQuery
     * @param results
     * @return
     */
    public static <E> LitePage<E> litePage(LitePageQuery litePageQuery, List<E> results) {
        return litePage(litePageQuery, litePageQuery.getRealResults(results), litePageQuery.hasNext(results.size()));
    }
    
    /**
     * 创建{@link CursorPage}。
     * 
     * @param <E>
     * @param <C>
     * @param cursorPageable
     * @param results
     * @param nextCursor
     * @param hasNext
     * @return
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageable<C> cursorPageable, List<E> results, C nextCursor, boolean hasNext) {
        return new CursorPageImpl<>(cursorPageable.getPageCursor(), cursorPageable.getPageSize(), results, nextCursor, hasNext);
    }
    
    /**
     * 创建{@link CursorPage}。
     * 
     * @param <E>
     * @param <C>
     * @param cursorPageQuery
     * @param results
     * @param cursorGetter
     * @return
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageQuery<C> cursorPageQuery, List<E> results, Function<E, C> cursorGetter) {
        List<E> realResults = cursorPageQuery.getRealResults(results);
        boolean hasNext = cursorPageQuery.hasNext(results.size());
        C nextCursor;
        if (realResults == null || realResults.isEmpty()) {
            // 没有查询到任何元素则保持游标不变，接下来的查询也将保持同样的结果
            nextCursor = cursorPageQuery.getPageCursor();
        } else {
            nextCursor = cursorGetter.apply(realResults.get(realResults.size() - 1));
        }
        return cursorPage(cursorPageQuery, realResults, nextCursor, hasNext);
    }
    
}
