package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;

import java.util.*;
import java.util.function.Function;

/**
 * 迭代器工具集门面。
 *
 * @author fengwk
 */
public class Iterators {

    private Iterators() {}

    /**
     * 将普通迭代器包装为NotNullElementIterator，但是迭代过程将受到检查，一旦违背性质将抛出异常。
     *
     * @param iterator not null
     * @param <E>
     * @return
     */
    public static <E extends Comparable<E>> NotNullElementIterator<E> checkNotNullElement(Iterator<E> iterator) {
        return new CheckedNotNullElementIterator<>(iterator);
    }

    /**
     * 将普通迭代器包装为OrderedIterator，但是迭代过程将受到检查，一旦违背性质将抛出异常。
     *
     * @param iterator not null
     * @param order not null
     * @param <E>
     * @return
     */
    public static <E extends Comparable<E>> OrderedIterator<E> checkOrder(Iterator<E> iterator, Order order) {
        return new CheckedOrderedIterator<>(iterator, order);
    }

    /**
     * 向迭代器尾部追加元素。
     *
     * @param iterator not null
     * @param appendElement
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> append(Iterator<E> iterator, E appendElement) {
        return append(iterator, Collections.singletonList(appendElement));
    }

    /**
     * 向迭代器尾部追加元素。
     *
     * @param iterator not null
     * @param appendElements
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> append(Iterator<E> iterator, List<E> appendElements) {
        List<Iterator<E>> iterators = new LinkedList<>();
        iterators.add(iterator);
        if (appendElements != null && !appendElements.isEmpty()) {
            iterators.add(appendElements.iterator());
        }
        return new ConnectIterator<>(iterators);
    }

    /**
     * 向迭代器头部追加元素。
     *
     * @param iterator not null
     * @param insertElement
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> insert(Iterator<E> iterator, E insertElement) {
        return insert(iterator, Collections.singletonList(insertElement));
    }

    /**
     * 向迭代器头部追加元素。
     *
     * @param iterator not null
     * @param insertElements
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> insert(Iterator<E> iterator, List<E> insertElements) {
        List<Iterator<E>> iterators = new LinkedList<>();
        if (insertElements != null && !insertElements.isEmpty()) {
            iterators.add(insertElements.iterator());
        }
        iterators.add(iterator);
        return new ConnectIterator<>(iterators);
    }

    /**
     * 连接多个迭代器。
     *
     * @param iterators not empty
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> connect(List<Iterator<E>> iterators) {
        return new ConnectIterator<>(iterators);
    }

    /**
     * 映射迭代元素。
     *
     * @param iterator not null
     * @param mapper not null
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> Iterator<T> map(Iterator<S> iterator, Function<S, T> mapper) {
        return new MapIterator<>(iterator, mapper);
    }

    /**
     * 将有序集合去重。
     *
     * @param iterator
     * @param <E>
     * @return
     */
    public static <E extends Comparable<E>> DistinctOrderedIterator<E> distinct(OrderedIterator<E> iterator) {
        return new DistinctOrderedIteratorImpl<>(iterator);
    }

    /**
     * 构建游标迭代器。
     *
     * @param cursorQueryFunc not null，唯一性游标查询函数。
     * @param cursorMapper not null，游标映射。
     * @param cursorOrder not null，游标顺序。
     * @param bufferSize 缓冲区大小。
     * @param firstCursor 首个游标位置。
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Comparable<C>> CursorIterator<E, C> cursor(CursorQueryFunction<E, C> cursorQueryFunc,
                                                                           Function<E, C> cursorMapper,
                                                                           Order cursorOrder,
                                                                           int bufferSize,
                                                                           C firstCursor) {
        return new CursorIteratorImpl<>(cursorQueryFunc, cursorMapper, cursorOrder, bufferSize, firstCursor);
    }

    /**
     * 构建游标迭代器。
     *
     * @param cursorQueryFunc not null，唯一性游标查询函数。
     * @param cursorMapper not null，游标映射。
     * @param cursorOrder not null，游标顺序。
     * @param bufferSize 缓冲区大小。
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Comparable<C>> CursorIterator<E, C> cursor(CursorQueryFunction<E, C> cursorQueryFunc,
                                                                           Function<E, C> cursorMapper,
                                                                           Order cursorOrder,
                                                                           int bufferSize) {
        return new CursorIteratorImpl<>(cursorQueryFunc, cursorMapper, cursorOrder, bufferSize, null);
    }

    /**
     * 使迭代器支持查看与退回操作。
     *
     * @param iterator not null
     * @param putBackCapacity 指定putBack容量，必须大于0，例如指定了10就意味着我们最多只支持putBack10次，若超出则会抛出异常，这一参数能显著优化程序的内存占用量。
     * @param <E>
     * @return
     */
    public static <E> PeekBackIterator<E> peekBack(Iterator<E> iterator, int putBackCapacity) {
        return new PeekBackIteratorImpl<>(iterator, putBackCapacity);
    }

    /**
     * 使迭游标迭代器支持查看与退回操作。
     *
     * @param iterator not null
     * @param putBackCapacity 指定putBack容量，必须大于0，例如指定了10就意味着我们最多只支持putBack10次，若超出则会抛出异常，这一参数能显著优化程序的内存占用量。
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Comparable<C>> PeekBackCursorIterator<E, C> peekBack(CursorIterator<E, C> iterator, int putBackCapacity) {
        return new PeekBackCursorIteratorImpl<>(iterator, putBackCapacity);
    }

    /**
     * 获取多个有序迭代器交集。
     *
     * @param iterators not empty，并且各个迭代器要有一致的顺序。
     * @param <E> 迭代元素。
     * @return 具有唯一性且有序的迭代器。
     */
    public static <E extends Comparable<E>> DistinctOrderedIterator<E> union(Collection<OrderedIterator<E>> iterators) {
        return new UnionIterator<>(iterators);
    }

    /**
     * 获取多个游标迭代器交集。
     *
     * @param iterators not empty，
     * @param lowerFunc not null
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Comparable<C>> Iterator<E> cursorUnion(Collection<CursorIterator<E, C>> iterators, Function<C, C> lowerFunc) {
        return new UnionCursorIterator<>(iterators, lowerFunc);
    }

    /**
     * 获取同步的迭代器。
     *
     * @param iterator not null
     * @param <E>
     * @return
     */
    public static <E> Iterator<E> sync(Iterator<E> iterator) {
        return new SynchronizedIterator<>(iterator);
    }

}
