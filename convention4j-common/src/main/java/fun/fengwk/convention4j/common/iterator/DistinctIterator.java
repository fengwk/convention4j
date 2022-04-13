package fun.fengwk.convention4j.common.iterator;

/**
 * 唯一性元素迭代器。
 *
 * @param <E> 迭代元素。
 *
 * @author fengwk
 */
public interface DistinctIterator<E> extends NotNullElementIterator<E> {

    /**
     * 唯一性元素迭代器所返回的每一个元素应当满足唯一性约束。
     *
     * @return
     * @throws java.util.NoSuchElementException 如果没有下一个元素将抛出该异常。
     */
    @Override
    E next();

}
