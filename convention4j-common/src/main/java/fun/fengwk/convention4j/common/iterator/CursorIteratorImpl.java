package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import fun.fengwk.convention4j.common.util.Order;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author fengwk
 */
class CursorIteratorImpl<E, C extends Comparable<C>> implements CursorIterator<E, C> {

    /**
     * 游标查询函数。
     */
    private final CursorQueryFunction<E, C> cursorQueryFunc;

    /**
     * 将元素映射为游标。
     */
    private final Function<E, C> cursorMapper;

    /**
     * 游标顺序。
     */
    private final Order cursorOrder;

    /**
     * 游标查询的缓冲区大小，该值应当大于等于1。
     */
    private final int bufferSize;

    /**
     * 缓冲区队列。
     */
    private final LinkedList<E> bufferQueue = new LinkedList<>();

    /**
     * 当前游标。
     */
    private C cursor;

    /**
     * 是否还能进行更多的游标查询。
     */
    private boolean hasNextQuery = true;

    /**
     *
     * @param cursorQueryFunc 唯一性游标查询函数。
     * @param cursorMapper 游标映射。
     * @param cursorOrder 游标顺序。
     * @param bufferSize 缓冲区大小。
     * @param firstCursor 首个游标位置。
     */
    CursorIteratorImpl(CursorQueryFunction<E, C> cursorQueryFunc,
                       Function<E, C> cursorMapper,
                       Order cursorOrder,
                       int bufferSize,
                       C firstCursor) {
        Objects.requireNonNull(cursorQueryFunc);
        Objects.requireNonNull(cursorMapper);
        Objects.requireNonNull(cursorOrder);
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Buffer size must be greater than or equal to one");
        }

        this.cursorQueryFunc = cursorQueryFunc;
        this.cursorMapper = cursorMapper;
        this.cursorOrder = cursorOrder;
        this.bufferSize = bufferSize;
        this.cursor = firstCursor;
    }

    @Override
    public boolean hasNext() {
        if (hasNextQuery && bufferQueue.isEmpty()) {
            List<E> result;
            try {
                result = cursorQueryFunc.query(cursor, bufferSize);
            } catch (Throwable err) {
                throw new RuntimeExecutionException(err);
            }
            if (result.size() < bufferSize) {
                hasNextQuery = false;
            }
            result.forEach(bufferQueue::addLast);
        }
        return !bufferQueue.isEmpty();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }

        E next = bufferQueue.removeFirst();
        if (next == null) {
            // for consistent
            bufferQueue.addFirst(next);
            throw new IllegalStateException("Element cannot be null");
        }

        C nextCursor = cursorMapper.apply(next);
        if (nextCursor == null) {
            // for consistent
            bufferQueue.addFirst(next);
            throw new IllegalStateException(String.format("Element '%s' map cursor cannot be null", next));
        }

        if (cursor == null) {
            cursor = nextCursor;
        } else if (nextCursor.compareTo(cursor) != 0) {
            if (!cursorOrder.isOrdered(cursor, nextCursor)) {
                // for consistent
                bufferQueue.addFirst(next);
                throw new IllegalStateException(String.format("Cursor '%s' and '%s' out of order '%s'", cursor, nextCursor, cursorOrder));
            }
            cursor = nextCursor;
        } else {
            // for consistent
            bufferQueue.addFirst(next);
            throw new IllegalStateException(String.format("Not unique cursor '%s'", nextCursor));
        }

        return next;
    }

    @Override
    public Order cursorOrder() {
        return cursorOrder;
    }

    /**
     * 重置游标位置。
     *
     * @param cursor
     */
    @Override
    public boolean resetCursor(C cursor) {
        if (this.cursor.compareTo(cursor) != 0) {
            this.cursor = cursor;
            this.bufferQueue.clear();
            this.hasNextQuery = true;
            return true;
        }

        return false;
    }

    /**
     * 获取当前游标。
     *
     * @return
     */
    @Override
    public C getCursor() {
        return cursor;
    }

}
