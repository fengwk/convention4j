package fun.fengwk.convention4j.common.iterator;

import java.util.*;

/**
 * 该迭代器用于连接多个迭代器。
 *
 * @author fengwk
 */
class ConnectIterator<E> implements Iterator<E> {

    private final LinkedList<Iterator<E>> iterators;
    private Iterator<E> cur;

    ConnectIterator(Collection<Iterator<E>> iterators) {
        if (iterators == null || iterators.isEmpty()) {
            throw new IllegalArgumentException("Iterators cannot be null or empty");
        }

        this.iterators = new LinkedList<>(Objects.requireNonNull(iterators));
        this.cur = this.iterators.removeFirst();
    }

    @Override
    public boolean hasNext() {
        while (!cur.hasNext() && !iterators.isEmpty()) {
            cur = iterators.removeFirst();
        }
        return cur.hasNext();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }

        return cur.next();
    }

}
