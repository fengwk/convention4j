package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.Order;
import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;

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
     * 检查当前迭代时是否还有更多元素，如果有返回true，否则返回false。
     * 一旦返回了true，{@link #next()}函数就一定不会抛出{@link NoSuchElementException}。
     *
     * @return
     * @throws RuntimeExecutionException 任何{@link CursorQueryFunction}导致的异常将会作为该异常的cause。
     */
    @Override
    boolean hasNext();

    /**
     * 获取下一迭代元素，该元素是{@link #getCursor()}下一个位置游标所关联的元素。
     *
     * @return
     * @throws NoSuchElementException 如果不存在下一元素则抛出该异常。
     * @throws IllegalStateException 以下情况将抛出该异常，异常的message中应该能够描述具体的异常情况：
     * <ul>
     * <li>如果发现空元素。</li>
     * <li>如果从元素映射为游标的过程中发生错误。</li>
     * <li>如果发现顺序错误的游标。</li>
     * <li>如果游标不唯一。</li>
     * </ul>
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
