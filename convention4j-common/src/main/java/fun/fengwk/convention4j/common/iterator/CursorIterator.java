package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 游标迭代器。
 *
 * @param <E> 迭代元素。
 * @param <C> 游标，应当具备唯一性和单调性。
 *
 * @author fengwk
 */
public interface CursorIterator<E, C extends Comparable<C>> extends Iterator<E> {

    /**
     * 获取下一迭代元素，该元素是{@link #getCursor()}下一个位置游标所关联的元素。
     *
     * @return
     * @throws NoSuchElementException 如果不存在下一元素则抛出该异常。
     * @throws IllegalStateException 如果发生迭代状态异常将抛出该异常。
     */
    @Override
    E next();

    /**
     * 获取游标顺序。
     *
     * @return
     */
    Order cursorOrder();

    /**
     * 重置游标位置。
     *
     * @param cursor
     * @return 如果改变了游标位置返回true。
     */
    boolean resetCursor(C cursor);

    /**
     * 获取当前游标。
     *
     * @return
     */
    C getCursor();

}
