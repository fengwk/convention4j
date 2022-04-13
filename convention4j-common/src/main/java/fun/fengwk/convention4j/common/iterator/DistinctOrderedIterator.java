package fun.fengwk.convention4j.common.iterator;

/**
 * 唯一且有序的迭代器。
 *
 * @param <E> 迭代元素。
 *
 * @author fengwk
 */
public interface DistinctOrderedIterator<E extends Comparable<E>> extends DistinctIterator<E>, OrderedIterator<E> {

    /**
     * 返回元素要同时满足唯一性和{@link #order()}定义的单调性。。
     *
     * @return
     * @throws java.util.NoSuchElementException 如果没有下一个元素将抛出该异常。
     */
    @Override
    E next();

}
