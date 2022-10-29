package fun.fengwk.convention4j.common.page;

import java.util.Collections;
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
     * @param pageQuery not null
     * @param results not null
     * @param totalCount >= 0
     * @return
     * @param <E> 元素类型。
     */
    public static <E> Page<E> page(PageQuery pageQuery, List<E> results, long totalCount) {
        return new PageImpl<>(pageQuery.getPageNumber(), pageQuery.getPageSize(), results, totalCount);
    }

    /**
     * 创建空的{@link Page}。
     *
     * @param pageQuery not null
     * @return
     * @param <E> 元素类型。
     */
    public static <E> Page<E> emptyPage(PageQuery pageQuery) {
        return new PageImpl<>(pageQuery.getPageNumber(), pageQuery.getPageSize(), Collections.emptyList(), 0);
    }
    
    /**
     * 创建{@link LitePage}。
     *
     * @param litePageQuery not null
     * @param results not null
     * @param hasNext
     * @return
     * @param <E> 元素类型。
     */
    public static <E> LitePage<E> litePage(LitePageQuery litePageQuery, List<E> results, boolean hasNext) {
        return new LitePageImpl<>(litePageQuery.getPageNumber(), litePageQuery.getPageSize(), results, hasNext);
    }
    
    /**
     * 创建{@link LitePage}。
     * 
     * @param litePageQuery not null
     * @param results not null
     * @return
     * @param <E> 元素类型。
     */
    public static <E> LitePage<E> litePage(LitePageQuery litePageQuery, List<E> results) {
        return litePage(litePageQuery, litePageQuery.getRealResults(results), litePageQuery.hasNext(results.size()));
    }

    /**
     * 创建空的{@link LitePage}。
     *
     * @param litePageQuery not null
     * @return
     * @param <E> 元素类型。
     */
    public static <E> LitePage<E> emptyLitePage(LitePageQuery litePageQuery) {
        return new LitePageImpl<>(litePageQuery.getPageNumber(), litePageQuery.getPageSize(),
                Collections.emptyList(), false);
    }

    /**
     * 创建{@link CursorPage}。
     *
     * @param cursorPageable not null，游标分页器。
     * @param results not null，游标分页查询出的结果集。
     * @param nextCursor 下一页的游标。
     * @param hasNext 是否有下一页。
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageQuery<C> cursorPageable, List<E> results,
                                                     C nextCursor, boolean hasNext) {
        return new CursorPageImpl<>(cursorPageable.getPageCursor(), cursorPageable.getPageSize(), results,
                nextCursor, hasNext);
    }

    /**
     * 创建{@link CursorPage}，该方法将自动把从结果集最后一个元素中获取到的游标作为下一页游标。
     *
     * @param cursorPageQuery 游标分页器。
     * @param results 查询出的结果集。
     * @param cursorGetter 从元素中获取游标的方法。
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageQuery<C> cursorPageQuery, List<E> results,
                                                     Function<E, C> cursorGetter) {
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

    /**
     * 创建空的{@link CursorPage}。
     *
     * @param cursorPageable not null
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> emptyCursorPage(CursorPageQuery<C> cursorPageable) {
        return new CursorPageImpl<>(cursorPageable.getPageCursor(), cursorPageable.getPageSize(),
                Collections.emptyList(), cursorPageable.getPageCursor(), false);
    }

}
