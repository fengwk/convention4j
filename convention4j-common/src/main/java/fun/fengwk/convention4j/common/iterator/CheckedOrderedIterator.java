package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;

import java.util.Iterator;
import java.util.Objects;

/**
 * 受检的有序的迭代器。
 *
 * @author fengwk
 */
class CheckedOrderedIterator<E extends Comparable<E>>
        extends CheckedNotNullElementIterator<E> implements OrderedIterator<E> {

    private final Order order;
    private E prev;

    CheckedOrderedIterator(Iterator<E> iterator, Order order) {
        super(iterator);
        this.order = Objects.requireNonNull(order);
    }

    public Order order() {
        return order;
    }

    @Override
    public E next() {
        E next = super.next();

        if (prev != null && !order.isOrdered(prev, next)) {
            ex = new IllegalStateException(String.format("Element %s and %s are out of order", prev, next));
            throw ex;
        }

        prev = next;
        return next;
    }

}
