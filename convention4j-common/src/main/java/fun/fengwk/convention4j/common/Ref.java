package fun.fengwk.convention4j.common;

/**
 * 对象引用。在java语言中缺少指针类型，使用Ref可以弥补这一缺失。
 * <pre> {@code
 *     Ref<Object> ref = Ref.of(obj);
 *     invoke(ref);
 * } </pre>
 *
 * @author fengwk
 */
public class Ref<T> {

    public T value;

    private Ref(T value) {
        this.value = value;
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

}
