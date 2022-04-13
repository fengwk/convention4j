package fun.fengwk.convention4j.common.iterator;

/**
 * 同步的支持查看与退回操作的游标迭代器。
 *
 * @author fengwk
 */
class SynchronizedPeekBackCursorIterator<E, C extends Comparable<C>> implements PeekBackCursorIterator<E, C> {

    private final PeekBackCursorIterator<E, C> iterator;

    SynchronizedPeekBackCursorIterator(PeekBackCursorIterator<E, C> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return iterator.next();
    }

    @Override
    public Order cursorOrder() {
        return iterator.cursorOrder();
    }

    @Override
    public boolean resetCursor(C cursor) {
        return iterator.resetCursor(cursor);
    }

    @Override
    public C getCursor() {
        return iterator.getCursor();
    }

    @Override
    public boolean canPutBack() {
        return iterator.canPutBack();
    }

    @Override
    public void putBack() {
        iterator.putBack();
    }

    @Override
    public E peek() {
        return iterator.peek();
    }

    @Override
    public E next(boolean drop) {
        return iterator.next(drop);
    }

}
