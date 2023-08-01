package fun.fengwk.convention4j.api.page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * 分页查询结果。
 *
 * @param <T> 查询结果类型。
 *
 * @author fengwk
 */
public interface Page<T> extends Serializable {

    /**
     * 获取当前页码。
     *
     * @return 当前页码。
     */
    int getPageNumber();

    /**
     * 获取当前页面大小。
     *
     * @return 当前页面大小。
     */
    int getPageSize();

    /**
     * 判断是否有上一页。
     *
     * @return 是否有上一页。
     */
    boolean hasPrev();

    /**
     * 判断是否有下一页。
     *
     * @return 是否有下一页。
     */
    boolean hasNext();

    /**
     * 获取总数。
     *
     * @return 总数。
     */
    long getTotalCount();

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    long getTotalPages();

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
     * @return 转换后的分页结果。
     * @param <S> 目标类型。
     */
    <S> Page<S> map(Function<? super T, ? extends S> mapper);

    /**
     * 通过mapper将所有当前分页结果转换为另外的分页结果。
     *
     * @param mapper 映射函数。
     * @return 转换后的分页结果。
     * @param <S> 目标类型。
     */
    <S> Page<S> mapAll(Function<? super List<T>, ? extends List<S>> mapper);

}
