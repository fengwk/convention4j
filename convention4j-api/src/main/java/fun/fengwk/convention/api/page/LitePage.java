package fun.fengwk.convention.api.page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * {@link LitePage}是{@link Page}的轻量级版本，省去了total相关的信息返回，使用{@link LitePageQuery}能够方便地进行相应查询。
 * 
 * @author fengwk
 */
public interface LitePage<T> extends Serializable {
    
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
    <S> LitePage<S> map(Function<? super T, ? extends S> mapper);

}
