package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.Objects;

/**
 * 受检的非空元素迭代器。
 *
 * @author fengwk
 */
class CheckedNotNullElementIterator<E> implements NotNullElementIterator<E> {

    private final Iterator<E> iterator;
    protected IllegalStateException ex;

    CheckedNotNullElementIterator(Iterator<E> iterator) {
        this.iterator = Objects.requireNonNull(iterator);
    }

    @Override
    public boolean hasNext() {
        if (ex != null) {
            throw ex;
        }

        return iterator.hasNext();
    }

    @Override
    public E next() {
        if (ex != null) {
            throw ex;
        }

        E next = iterator.next();

        if (next == null) {
            ex = new IllegalStateException("found null element");
            throw ex;
        }

        return next;
    }

}
