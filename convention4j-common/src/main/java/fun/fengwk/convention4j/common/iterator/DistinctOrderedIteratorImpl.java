package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 该迭代器为OrderedIterator添加唯一性。
 *
 * @author fengwk
 */
class DistinctOrderedIteratorImpl<E extends Comparable<E>> implements DistinctOrderedIterator<E> {

    private final OrderedIterator<E> iterator;
    private E prev;
    private E next;

    DistinctOrderedIteratorImpl(OrderedIterator<E> iterator) {
        this.iterator = Objects.requireNonNull(iterator);
    }

    @Override
    public Order order() {
        return iterator.order();
    }

    @Override
    public boolean hasNext() {
        while (next == null && iterator.hasNext()) {
            E directNext = iterator.next();
            if (prev == null || prev.compareTo(directNext) != 0) {
                next = directNext;
            }
        }

        return next != null;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }

        prev = next;
        next = null;

        return prev;
    }

}
