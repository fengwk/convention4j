package fun.fengwk.convention4j.common.iterator;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author fengwk
 */
class PeekBackCursorIteratorImpl<E, C extends Comparable<C>> implements PeekBackCursorIterator<E, C> {

    private final CursorIterator<E, C> iterator;
    private final LinkedList<E> bufferQueue = new LinkedList<>();
    private final LinkedList<E> putBackStack = new LinkedList<>();
    private final LinkedList<C> cursorBufferQueue = new LinkedList<>();
    private final LinkedList<C> cursorPutBackStack = new LinkedList<>();
    private final int putBackCapacity;

    PeekBackCursorIteratorImpl(CursorIterator<E, C> iterator, int putBackCapacity) {
        if (putBackCapacity <= 0) {
            throw new IllegalArgumentException("Put back capacity must be greater than zero");
        }

        this.iterator = Objects.requireNonNull(iterator);
        this.putBackCapacity = putBackCapacity;
        cursorBufferQueue.addLast(iterator.getCursor());
    }

    @Override
    public boolean hasNext() {
        if (bufferQueue.isEmpty() && iterator.hasNext()) {
            bufferQueue.addLast(iterator.next());
            cursorBufferQueue.addLast(iterator.getCursor());
        }

        return !bufferQueue.isEmpty();
    }

    @Override
    public E next() {
        return next(false);
    }

    @Override
    public Order cursorOrder() {
        return iterator.cursorOrder();
    }

    @Override
    public boolean resetCursor(C cursor) {
        if (iterator.resetCursor(cursor)) {
            bufferQueue.clear();
            putBackStack.clear();
            cursorBufferQueue.clear();
            cursorPutBackStack.clear();
            cursorBufferQueue.addLast(iterator.getCursor());
            return true;
        }

        return false;
    }

    @Override
    public C getCursor() {
        return cursorBufferQueue.getFirst();
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
        cursorBufferQueue.addFirst(cursorPutBackStack.removeFirst());
    }

    @Override
    public E peek() {
        E next = next();
        putBack();
        return next;
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
                cursorPutBackStack.removeLast();
            }
            putBackStack.addFirst(next);
            cursorPutBackStack.addFirst(cursorBufferQueue.removeFirst());
        }
        return next;
    }

}
