package fun.fengwk.convention4j.common.tika;

import org.apache.tika.Tika;

/**
 * Tika可能是向线程安全设计的，但仍然可能存在一些潜在的BUG导致其线程不安全，因此使用ThreadLocal保护。
 *
 * @author fengwk
 */
public class ThreadLocalTika {

    private static final ThreadLocal<Tika> TL = ThreadLocal.withInitial(Tika::new);

    private ThreadLocalTika() {}

    public static Tika current() {
        return TL.get();
    }

}
