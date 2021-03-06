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
     * 获取当页游标，如果是首页则为null。
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
     * 获取下一页游标，下一页的游标即为当前页面最后一个元素的游标属性，如果没有则为null。
     * 
     * @return
     */
    @Nullable
    C getNextCursor();
    
    /**
     * 是否有下一页。
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
     * 通过mapper将当前分页结果转换为另外的分页结果。
     * 
     * @param <S>
     * @param mapper not null
     * @return
     */
    <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper);

}
