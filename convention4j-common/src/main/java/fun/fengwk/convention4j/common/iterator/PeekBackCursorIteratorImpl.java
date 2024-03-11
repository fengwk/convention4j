package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;

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
        return !bufferQueue.isEmpty() || iterator.hasNext();
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

        E next;
        C cursor;
        if (!bufferQueue.isEmpty()) {
            next = bufferQueue.removeFirst();
            cursor = cursorBufferQueue.removeFirst();
        } else {
            next = iterator.next();
            cursorBufferQueue.addLast(iterator.getCursor());
            cursor = cursorBufferQueue.removeFirst();
        }

        if (!drop) {
            while (putBackStack.size() >= putBackCapacity) {
                putBackStack.removeLast();
                cursorPutBackStack.removeLast();
            }
            putBackStack.addFirst(next);
            cursorPutBackStack.addFirst(cursor);
        }

        return next;
    }

}
