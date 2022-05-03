package fun.fengwk.convention4j.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可以生产指定名称线程的线程工厂。
 *
 * @author fengwk
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String name) {
        this.name = name;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, String.format("%s-%d", name, threadNumber.getAndIncrement()), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
