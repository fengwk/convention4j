package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 求多个DistinctOrderedIterator的交集。
 *
 * @author fengwk
 */
class UnionIterator<E extends Comparable<E>> implements DistinctOrderedIterator<E> {

    private final Order order;
    private final List<PeekBackIterator<E>> iterators;
    private E next;

    UnionIterator(Collection<? extends OrderedIterator<E>> iterators) {
        if (iterators == null || iterators.isEmpty()) {
            throw new IllegalArgumentException("Iterators cannot be null or empty");
        }

        Order order = null;
        List<PeekBackIterator<E>> peekBackIterators = new ArrayList<>();
        for (OrderedIterator<E> iter : iterators) {
            if (order == null) {
                order = iter.order();
            } else if (order != iter.order()) {
                throw new IllegalArgumentException(String.format("Iterators '%s' out of order", iter));
            }

            DistinctOrderedIterator<E> distinctOrderedIterator;
            if (iter instanceof DistinctOrderedIterator) {
                distinctOrderedIterator = (DistinctOrderedIterator<E>) iter;
            } else {
                distinctOrderedIterator = new DistinctOrderedIteratorImpl<>(iter);
            }
            peekBackIterators.add(new PeekBackIteratorImpl<>(distinctOrderedIterator, 1));
        }

        this.order = order;
        this.iterators = peekBackIterators;
    }

    @Override
    public boolean hasNext() {
        outLoop: while (next == null) {
            for (PeekBackIterator<E> iter : iterators) {
                if (!iter.hasNext()) {
                    break outLoop;
                }
            }

            E maxNext = null;
            for (PeekBackIterator<E> iter : iterators) {
                E next = iter.peek();
                if (maxNext == null || (order == Order.ASC ? next.compareTo(maxNext) > 0 : next.compareTo(maxNext) < 0)) {
                    maxNext = next;
                }
            }

            // TODO for test
            assert maxNext != null;

            int eqCount = 0;
            for (PeekBackIterator<E> iter : iterators) {
                // 定位到lower(maxNext)位置
                while (iter.hasNext() && (order == Order.ASC ? iter.peek().compareTo(maxNext) < 0 : iter.peek().compareTo(maxNext) > 0)) {
                    iter.next();
                }

                if (iter.hasNext() && iter.peek().compareTo(maxNext) == 0) {
                    iter.next();
                    eqCount++;
                }
            }

            if (eqCount == iterators.size()) {
                next = maxNext;
            }
        }

        return next != null;
    }

    @Override
    public Order order() {
        return order;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }

        E ret = next;
        next = null;

        return ret;
    }

}
