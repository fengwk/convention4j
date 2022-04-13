package fun.fengwk.convention4j.api.page;

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
     * 获取当页游标。
     * 
     * @return
     */
    C getPageCursor();

    /**
     * 获取页面大小，范围[1, +∞)。
     * 
     * @return
     */
    int getPageSize();
    
    /**
     * 获取下一页游标。
     * 
     * @return
     */
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
     * @param mapper
     * @return
     */
    <S> CursorPage<S, C> map(Function<? super T, ? extends S> mapper);

}
