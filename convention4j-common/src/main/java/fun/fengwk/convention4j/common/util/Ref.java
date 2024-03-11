package fun.fengwk.convention4j.common.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * 对象引用。在java语言中缺少指针类型，使用Ref可以弥补这一缺失。
 * <pre> {@code
 *     Ref<Object> ref = Ref.of(obj);
 *     invoke(ref);
 * } </pre>
 *
 * @author fengwk
 */
public final class Ref<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T value;

    private Ref(T value) {
        this.value = value;
    }

    /**
     * 构造指向指定value的引用。
     *
     * @param <T>
     * @param value
     * @return
     */
    public static <T> Ref<T> of(T value) {
        return new Ref<>(value);
    }

    /**
     * 构造空引用。
     * 
     * @param <T>
     * @return
     */
    public static <T> Ref<T> empty() {
        return new Ref<>(null);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ref<?> ref = (Ref<?>) o;
        return Objects.equals(value, ref.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
