package fun.fengwk.convention4j.common.iterator;

import java.util.List;

/**
 * 游标查询函数。
 *
 * @param <E> 迭代元素。
 * @param <C> 游标，应当具备唯一性和单调性。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface CursorQueryFunction<E, C extends Comparable<C>> {

    /**
     * 查询大于游标cursor的limit个元素列表。
     *
     * @param cursor 游标，应当具备唯一性和单调性，如果为null表示从头开始查询。
     * @param limit 查询限制数量。
     * @return
     */
    List<E> query(C cursor, int limit);

}
