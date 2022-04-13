package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 同步的迭代器。
 *
 * @author fengwk
 */
class SynchronizedIterator<E> implements Iterator<E> {

    private final Iterator<E> iterator;

    SynchronizedIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public synchronized boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public synchronized E next() {
        return iterator.next();
    }

    @Override
    public synchronized void remove() {
        iterator.remove();
    }

    @Override
    public synchronized void forEachRemaining(Consumer<? super E> action) {
        iterator.forEachRemaining(action);
    }

}
