package fun.fengwk.convention4j.common.page;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * {@link CursorPage}包装了游标分页查询的结果集。
 *
 * @author fengwk
 */
public interface CursorPage<T, C> extends Serializable {
    
    /**
     * 获取当页游标，如果是首页则为null或一个哨兵值（推荐使用null）。
     * 
     * @return
     */
    @Nullable
    C getPageCursor();

    /**
     * 获取页面大小，范围[1, +∞)。
     * 
     * @return
     */
    int getPageSize();
    
    /**
     * 获取下一页游标，在下一次进行游标查询时应当携带该游标进行后续查询。
     * 
     * @return
     */
    @Nullable
    C getNextCursor();
    
    /**
     * 是否有下一页，这是决定游标分页是否已结束的唯一条件。
     * 
     * @return
     */
    boolean hasNext();

    /**
     * 获取结果集列表，如果没有结果则返回空列表。
     * 
     * @return
     */
    List<T> getResults();

    /**
     * 通过mapper将一项当前分页结果转换为另外的分页结果。
     *
     * @param mapper
     * @return
     * @param <S> 目标类型
     */
    <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper);

    /**
     * 通过mapper将所有当前分页结果转换为另外的分页结果。
     *
     * @param mapper
     * @return
     * @param <S> 目标类型
     */
    <S> CursorPage<S, C> mapAll(Function<? super List<T>, ? extends List<S>> mapper);

}
