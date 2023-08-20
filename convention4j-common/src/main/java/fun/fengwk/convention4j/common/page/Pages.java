package fun.fengwk.convention4j.common.page;

import fun.fengwk.convention4j.api.page.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link Pages}提供了一系列分页查询需要的工具方法。
 * 
 * @author fengwk
 */
public class Pages {

    /**
     * 指定最大的限制
     */
    private static volatile int maxLimit = 1000;
    
    private Pages() {}

    /**
     * 修改最大的分页限制量。
     *
     * @param maxLimit 最大分页限制量。
     */
    public static void setMaxLimit(int maxLimit) {
        Pages.maxLimit = maxLimit;
    }

    /**
     * 获取分页查询器的查询时偏移量。
     *
     * @param pageQuery 分页查询器。
     * @return 查询偏移量。
     */
    public static long queryOffset(PageQuery pageQuery) {
        int offset = (pageQuery.getPageNumber() - 1) * getPageSize(pageQuery);
        return Math.max(offset, 0);
    }

    /**
     * 获取分页查询器的查询时限制。
     *
     * @param pageQuery 分页查询器。
     * @return 查询时限制。
     */
    public static int queryLimit(PageQuery pageQuery) {
        int limit = getPageSize(pageQuery);
        limit = Math.max(limit, 0);
        return limit;
    }

    /**
     * 获取游标分页查询器的查询时限制，为解析是否存在下一页会多查询一条记录。
     *
     * @param cursorPageQuery 游标分页查询器。
     * @return 查询时限制。
     */
    public static <C> int queryLimit(CursorPageQuery<C> cursorPageQuery) {
        int limit = getLimit(cursorPageQuery);
        limit = Math.max(limit, 0);
        return limit + 1;
    }

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
        return new DefaultPage<>(pageQuery.getPageNumber(), getPageSize(pageQuery), results, totalCount);
    }

    /**
     * 创建空的{@link Page}。
     *
     * @param pageQuery not null
     * @return
     * @param <E> 元素类型。
     */
    public static <E> Page<E> emptyPage(PageQuery pageQuery) {
        return page(pageQuery, Collections.emptyList(), 0);
    }

    /**
     * 创建空的{@link Page}。
     *
     * @return
     * @param <E> 元素类型。
     */
    public static <E> Page<E> emptyPage() {
        PageQuery pageQuery = new PageQuery(1, 0);
        return emptyPage(pageQuery);
    }

    /**
     * 创建{@link CursorPage}。
     *
     * @param cursorPageQuery not null，游标分页器。
     * @param results not null，游标分页查询出的结果集。
     * @param nextCursor 下一页的游标。
     * @param hasNext 是否有下一页。
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageQuery<C> cursorPageQuery, List<E> results,
                                                     C nextCursor, boolean hasNext) {
        return new DefaultCursorPage<>(cursorPageQuery.getCursor(), getLimit(cursorPageQuery),
            results, nextCursor, hasNext);
    }

    /**
     * 创建{@link CursorPage}，该方法将自动把从结果集最后一个元素中获取到的游标作为下一页游标，
     * 另外为了保证hasNext的正确性必须使用{@link #queryLimit(CursorPageQuery)}作为查询时的限制数。
     *
     * @param cursorPageQuery 游标分页器。
     * @param results 查询出的结果集。
     * @param cursorGetter 从元素中获取游标的方法。
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> cursorPage(CursorPageQuery<C> cursorPageQuery,
                                                     List<E> results, Function<E, C> cursorGetter) {
        int limit = getLimit(cursorPageQuery);
        boolean hasNext = results.size() > limit;
        List<E> realResult = results.stream().limit(limit).collect(Collectors.toList());
        C nextCursor = realResult.isEmpty() ?
            cursorPageQuery.getCursor() : cursorGetter.apply(realResult.get(realResult.size() - 1));
        return cursorPage(cursorPageQuery, realResult, nextCursor, hasNext);
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
        return cursorPage(cursorPageable, Collections.emptyList(), cursorPageable.getCursor(), false);
    }

    /**
     * 创建空的{@link CursorPage}。
     *
     * @return
     * @param <E> 元素类型。
     * @param <C> 游标类型。
     */
    public static <E, C> CursorPage<E, C> emptyCursorPage() {
        CursorPageQuery<C> cursorPageQuery = new CursorPageQuery<>(null, 0);
        return emptyCursorPage(cursorPageQuery);
    }

    private static int getPageSize(PageQuery pageQuery) {
        return Math.min(pageQuery.getPageSize(), maxLimit);
    }

    private static <C> int getLimit(CursorPageQuery<C> cursorPageQuery) {
        return Math.min(cursorPageQuery.getLimit(), maxLimit);
    }

    public static String formatSqlOrderBy(List<Sort> sorts, Map<String, String> keyMap) {
        if (sorts == null || sorts.isEmpty()) {
            return null;
        }
        return sorts.stream().map(s -> formatSqlOrderBy(s, keyMap)).collect(Collectors.joining(", "));
    }

    private static String formatSqlOrderBy(Sort sort, Map<String, String> keyMap) {
        String key = keyMap.getOrDefault(sort.getKey(), sort.getKey());
        return sort.isAsc() ? key : key + " desc";
    }

}
