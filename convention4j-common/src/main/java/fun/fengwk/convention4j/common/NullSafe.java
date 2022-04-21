package fun.fengwk.convention4j.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * NullSafe可以简化空指针判断。
 *
 * @author fengwk
 */
public class NullSafe {

    private NullSafe() {}

    /**
     * 当obj不为null时进行映射操作。
     *
     * @param obj
     * @param mapper
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T map(S obj, Function<S, T> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }

    /**
     * 当collection不为null时进行映射操作。
     *
     * @param collection
     * @param mapper
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> map(Collection<S> collection, Function<S, T> mapper) {
        return collection == null ? null : collection.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 当obj为null时返回defaultObj。
     *
     * @param <T>
     * @param obj
     * @param defaultObj
     * @return
     */
    public static <T> T of(T obj, T defaultObj) {
        return obj == null ? defaultObj : obj;
    }

    /**
     * 当obj为null时返回defaultObjFactory创建的对象。
     *
     * @param <T>
     * @param obj
     * @param defaultObjFactory
     * @return
     */
    public static <T> T of(T obj, Supplier<T> defaultObjFactory) {
        return obj == null ? defaultObjFactory.get() : obj;
    }

    /**
     * 当collection为null时返回空集合。
     *
     * @param <E>
     * @param collection
     * @return
     */
    public static <E> Collection<E> of(Collection<E> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    /**
     * 当list为null时返回空列表。
     *
     * @param <E>
     * @param list
     * @return
     */
    public static <E> List<E> of(List<E> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 当set为null时返回空集合。
     *
     * @param <E>
     * @param set
     * @return
     */
    public static <E> Set<E> of(Set<E> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 当enumeration为null时返回空枚举。
     *
     * @param <E>
     * @param enumeration
     * @return
     */
    public static <E> Enumeration<E> of(Enumeration<E> enumeration) {
        return enumeration == null ? Collections.emptyEnumeration() : enumeration;
    }

    /**
     * 当iter为null时返回空迭代器。
     *
     * @param <E>
     * @param iter
     * @return
     */
    public static <E> Iterator<E> of(Iterator<E> iter) {
        return iter == null ? Collections.emptyIterator() : iter;
    }

    /**
     * 当iter为null时返回空列表迭代器。
     *
     * @param <E>
     * @param iter
     * @return
     */
    public static <E> ListIterator<E> of(ListIterator<E> iter) {
        return iter == null ? Collections.emptyListIterator() : iter;
    }

    /**
     * 当map为null时返回空映射。
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V> Map<K, V> of(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * 当map为null时返回空导向映射。
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V> NavigableMap<K, V> of(NavigableMap<K, V> map) {
        return map == null ? Collections.emptyNavigableMap() : map;
    }

    /**
     * 当map为null时返回空有序映射。
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V> SortedMap<K, V> of(SortedMap<K, V> map) {
        return map == null ? Collections.emptySortedMap() : map;
    }

    /**
     * 当set为null时返回空导向集合。
     *
     * @param <E>
     * @param set
     * @return
     */
    public static <E> NavigableSet<E> of(NavigableSet<E> set) {
        return set == null ? Collections.emptyNavigableSet() : set;
    }

    /**
     * 当set为null时返回空有序集合。
     *
     * @param <E>
     * @param set
     * @return
     */
    public static <E> SortedSet<E> of(SortedSet<E> set) {
        return set == null ? Collections.emptySortedSet() : set;
    }

    /**
     * 当byte[]为null时返回空byte[]。
     *
     * @param bytes
     * @return
     */
    public static byte[] of(byte[] bytes) {
        return bytes == null ? new byte[0] : bytes;
    }

    /**
     * 当short[]为null时返回空short[]。
     *
     * @param shorts
     * @return
     */
    public static short[] of(short[] shorts) {
        return shorts == null ? new short[0] : shorts;
    }

    /**
     * 当int[]为null时返回空int[]。
     *
     * @param ints
     * @return
     */
    public static int[] of(int[] ints) {
        return ints == null ? new int[0] : ints;
    }

    /**
     * 当long[]为null时返回空long[]。
     *
     * @param longs
     * @return
     */
    public static long[] of(long[] longs) {
        return longs == null ? new long[0] : longs;
    }

    /**
     * 当float[]为null时返回空float[]。
     *
     * @param floats
     * @return
     */
    public static float[] of(float[] floats) {
        return floats == null ? new float[0] : floats;
    }

    /**
     * 当double[]为null时返回空double[]。
     *
     * @param doubles
     * @return
     */
    public static double[] of(double[] doubles) {
        return doubles == null ? new double[0] : doubles;
    }

    /**
     * 当char[]为null时返回空char[]。
     *
     * @param chars
     * @return
     */
    public static char[] of(char[] chars) {
        return chars == null ? new char[0] : chars;
    }

    /**
     * 当boolean[]为null时返回空boolean[]。
     *
     * @param booleans
     * @return
     */
    public static boolean[] of(boolean[] booleans) {
        return booleans == null ? new boolean[0] : booleans;
    }

    /**
     * 当Object[]为null时返回空Object[]。
     *
     * @param objects
     * @return
     */
    public static Object[] of(Object[] objects) {
        return objects == null ? new Object[0] : objects;
    }

}
