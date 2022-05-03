package fun.fengwk.convention4j.common.iterator;

/**
 * 支持查看与退回操作的游标迭代器。
 *
 * @author fengwk
 */
public interface PeekBackCursorIterator<E, C extends Comparable<C>>
        extends CursorIterator<E, C>, PeekBackIterator<E> {

}
