package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 支持查看与退回操作的迭代器。
 *
 * @author fengwk
 */
class PeekBackIteratorImpl<E> implements PeekBackIterator<E> {

    private final Iterator<E> iterator;
    private final LinkedList<E> bufferQueue = new LinkedList<>();
    private final LinkedList<E> putBackStack = new LinkedList<>();
    private final int putBackCapacity;

    /**
     * 创建支持PeekBack的迭代器。
     *
     * @param iterator
     * @param putBackCapacity 指定putBack容量，必须大于0，例如指定了10就意味着我们最多只支持putBack10次，若超出则会抛出异常，这一参数能显著优化程序的内存占用量。
     */
    PeekBackIteratorImpl(Iterator<E> iterator, int putBackCapacity) {
        if (putBackCapacity < 1) {
            throw new IllegalArgumentException("Put back capacity must be greater than or equal to one");
        }

        this.iterator = Objects.requireNonNull(iterator);
        this.putBackCapacity = putBackCapacity;
    }

    @Override
    public boolean canPutBack() {
        return !putBackStack.isEmpty();
    }

    @Override
    public void putBack() {
        if (!canPutBack()) {
            throw new NoSuchElementException("No put back element");
        }

        bufferQueue.addFirst(putBackStack.removeFirst());
    }

    @Override
    public E peek() {
        E next = next();
        putBack();
        return next;
    }

    @Override
    public boolean hasNext() {
        if (bufferQueue.isEmpty() && iterator.hasNext()) {
            bufferQueue.addLast(iterator.next());
        }

        return !bufferQueue.isEmpty();
    }

    @Override
    public E next() {
        return next(false);
    }

    @Override
    public E next(boolean drop) {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }

        E next = bufferQueue.removeFirst();
        if (!drop) {
            while (putBackStack.size() >= putBackCapacity) {
                putBackStack.removeLast();
            }
            putBackStack.addFirst(next);
        }
        return next;
    }

}
