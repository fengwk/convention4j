package fun.fengwk.convention4j.api.page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * {@link Page}包装了分页查询的结果集。
 * 
 * @author fengwk
 */
public interface Page<T> extends Serializable {
    
    /**
     * 获取页码，范围[1, +∞)。
     * 
     * @return
     */
    int getPageNumber();

    /**
     * 获取页面大小，范围[1, +∞)。
     * 
     * @return
     */
    int getPageSize();
    
    /**
     * 是否有上一页。
     * 
     * @return
     */
    boolean hasPrev();
    
    /**
     * 是否有下一页。
     * 
     * @return
     */
    boolean hasNext();

    /**
     * 获取总数。
     * 
     * @return
     */
    long getTotalCount();
    
    /**
     * 获取总页数。
     * 
     * @return
     */
    long getTotalPages();

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
    <S> Page<S> map(Function<? super T, ? extends S> mapper);

}
