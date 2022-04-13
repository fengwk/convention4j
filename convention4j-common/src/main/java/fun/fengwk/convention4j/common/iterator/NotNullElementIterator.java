package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 非空元素迭代器。
 *
 * @param <E> 迭代元素。
 *
 * @author fengwk
 */
public interface NotNullElementIterator<E> extends Iterator<E> {

    /**
     * 获取下一个迭代元素且元素不为null。
     *
     * @return
     * @throws NoSuchElementException 如果没有元素可迭代将抛出该异常。
     */
    @Override
    E next();

}
