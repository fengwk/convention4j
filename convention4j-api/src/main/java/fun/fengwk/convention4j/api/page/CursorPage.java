package fun.fengwk.convention4j.api.page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * 游标分页查询的结果。
 *
 * @param <T> 查询结果类型。
 * @param <C> 游标类型。
 *
 * @author fengwk
 */
public interface CursorPage<T, C> extends Serializable {

    /**
     * 获取当前页游标。
     *
     * @return 当前页游标。
     */
    C getCursor();

    /**
     * 获取当前游标分页限制。
     *
     * @return 当前游标分页限制。
     */
    int getLimit();

    /**
     * 获取下一页游标。
     * 
     * @return 下一页游标。
     */
    C getNextCursor();
    
    /**
     * 是否还有更多数据，这是决定游标分页是否已结束的唯一条件。
     * 
     * @return 是否还有下一页。
     */
    boolean isMore();

    /**
     * 获取结果集列表，如果没有结果则返回空列表。
     * 
     * @return 结果列表。
     */
    List<T> getResults();

    /**
     * 通过mapper将一项当前分页结果转换为另外的分页结果。
     *
     * @param mapper 映射函数。
     * @return 转换后的游标分页结果。
     * @param <S> 目标类型。
     */
    <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper);

    /**
     * 通过mapper将所有当前分页结果转换为另外的分页结果。
     *
     * @param mapper 映射函数。
     * @return 转换后的游标分页结果。
     * @param <S> 目标类型。
     */
    <S> CursorPage<S, C> mapAll(Function<? super List<T>, ? extends List<S>> mapper);

}
