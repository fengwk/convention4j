package fun.fengwk.convention4j.common.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * 求多个CursorIterator交集。
 *
 * @author fengwk
 */
class UnionCursorIterator<E, C extends Comparable<C>> implements Iterator<E> {

    private final List<PeekBackCursorIterator<E, C>> iterators;
    private final Function<C, C> prevFunc;
    private final Order order;
    private E next;

    UnionCursorIterator(Collection<CursorIterator<E, C>> iterators, Function<C, C> prevFunc) {
        Objects.requireNonNull(iterators);
        Objects.requireNonNull(prevFunc);
        if (iterators.isEmpty()) {
            throw new IllegalArgumentException("Iterators cannot be empty");
        }

        Order order = null;
        List<PeekBackCursorIterator<E, C>> peekBackIterators = new ArrayList<>();
        for (CursorIterator<E, C> iter : iterators) {
            if (order == null) {
                order = iter.cursorOrder();
            } else if (order != iter.cursorOrder()) {
                throw new IllegalArgumentException("Iterators out of order");
            }

            if (iter instanceof SynchronizedIterator) {
                peekBackIterators.add((PeekBackCursorIterator<E, C>) iter);
            } if (iter instanceof PeekBackCursorIterator) {
                peekBackIterators.add(new SynchronizedPeekBackCursorIterator<>((PeekBackCursorIterator<E, C>) iter));
            } else {
                peekBackIterators.add(new SynchronizedPeekBackCursorIterator<>(new PeekBackCursorIteratorImpl<>(iter, 1)));
            }
        }

        this.iterators = peekBackIterators;
        this.prevFunc = prevFunc;
        this.order = order;
    }

    @Override
    public boolean hasNext() {
        while (next == null) {
            // 使用并行计算优化hasNext可能存在的IO操作
            if (!iterators.stream()
                    .map(PeekBackCursorIterator::hasNext)
                    .reduce((has1, has2) -> has1 && has2)
                    .get()) {
                break;
            }
//            for (PeekBackCursorIterator<E, C> iter : iterators) {
//                if (!iter.hasNext()) {
//                    break outLoop;
//                }
//            }

            C maxCursor = null;
            E maxCursorElement = null;
            for (PeekBackCursorIterator<E, C> iter : iterators) {
                E cursorElement = iter.next();
                C cursor = iter.getCursor();
                iter.putBack();
                if (maxCursor == null || (order == Order.ASC ? cursor.compareTo(maxCursor) > 0 : cursor.compareTo(maxCursor) < 0)) {
                    maxCursor = cursor;
                    maxCursorElement = cursorElement;
                }
            }

            // TODO for test
            assert maxCursor != null;
            assert maxCursorElement != null;

            int eqCount = 0;
            for (PeekBackCursorIterator<E, C> iter : iterators) {
                // 快速定位到lower(maxCursor)位置
                iter.next();
                C nextCursor = iter.getCursor();

                // TODO for test
//                assert nextCursor.compareTo(maxCursor) <= 0;

                if (nextCursor.compareTo(maxCursor) == 0) {
                    eqCount++;
                } else {
                    iter.resetCursor(prevFunc.apply(maxCursor));
                    if (iter.hasNext()) {
                        iter.next();
                        if (iter.getCursor().compareTo(maxCursor) == 0) {
                            eqCount++;
                        } else {
                            iter.putBack();
                        }
                    }
                }
            }

            if (eqCount == iterators.size()) {
                next = maxCursorElement;
            }
        }

        return next != null;
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
