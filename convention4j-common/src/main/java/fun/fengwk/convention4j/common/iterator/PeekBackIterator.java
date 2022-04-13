package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 支持查看与退回操作的迭代器。
 *
 * @param <E> 迭代元素。
 *
 * @author fengwk
 */
public interface PeekBackIterator<E> extends Iterator<E> {

    /**
     * 检查是否能够回退一个元素。
     *
     * @return
     */
    boolean canPutBack();

    /**
     * 回退一个元素。
     *
     * @throws NoSuchElementException 若无元素可回退将抛出该异常。
     */
    void putBack();

    /**
     * 查看下一元素。
     *
     * @return
     * @throws NoSuchElementException 若无下一元素将抛出该异常。
     */
    E peek();

    /**
     * 获取下一元素。
     *
     * @param drop true表示next元素不可回退，false表示可回退。
     * @return
     * @throws NoSuchElementException 如果没有元素可迭代将抛出该异常。
     */
    E next(boolean drop);

}
