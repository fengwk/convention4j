package fun.fengwk.convention4j.common;

import java.util.List;

/**
 * 简化列表操作，明确操作语义。
 *
 * @author fengwk
 */
public class ListUtils {

    private ListUtils() {}

    /**
     * 如果列表不为空，则获取列表的第一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E tryGetFirst(List<E> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /**
     * 如果列表不为空，则获取列表的最后一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E tryGetLast(List<E> list) {
        return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
    }

    /**
     * 获取列表的第一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E getFirst(List<E> list) {
        return list.get(0);
    }

    /**
     * 获取列表的最后一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E getLast(List<E> list) {
        return list.get(list.size() - 1);
    }

    /**
     * 在列表的第一个位置插入元素。
     *
     * @param <E>
     * @param list
     * @param element
     */
    public static <E> void addFirst(List<E> list, E element) {
        list.add(0, element);
    }

    /**
     * 在列表的最后一个位置插入元素。
     *
     * @param <E>
     * @param list
     * @param element
     */
    public static <E> void addLast(List<E> list, E element) {
        list.add(list.size(), element);
    }

    /**
     * 向列表的第一个位置设置元素。
     *
     * @param <E>
     * @param list
     * @param element
     * @return
     */
    public static <E> E setFirst(List<E> list, E element) {
        return list.set(0, element);
    }

    /**
     * 向列表的最后一个位置设置元素。
     *
     * @param <E>
     * @param list
     * @param element
     * @return
     */
    public static <E> E setLast(List<E> list, E element) {
        return list.set(list.size() - 1, element);
    }

    /**
     * 移除列表的第一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E removeFirst(List<E> list) {
        return list.remove(0);
    }

    /**
     * 移除列表的最后一个元素。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> E removeLast(List<E> list) {
        return list.remove(list.size() - 1);
    }

}
