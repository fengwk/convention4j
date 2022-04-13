package fun.fengwk.convention4j.common.iterator;

/**
 * 有序的迭代器。
 *
 * @param <E> 迭代元素。
 *
 * @author fengwk
 */
public interface OrderedIterator<E extends Comparable<E>> extends NotNullElementIterator<E> {

    /**
     * 获取当前迭代器顺序。
     *
     * @return
     */
    Order order();

    /**
     * 有序迭代器获取的所有元素都应当符合{@link #order()}定义的单调性。
     *
     * @return
     * @throws java.util.NoSuchElementException 没有更多元素将抛出该异常
     */
    @Override
    E next();

}
