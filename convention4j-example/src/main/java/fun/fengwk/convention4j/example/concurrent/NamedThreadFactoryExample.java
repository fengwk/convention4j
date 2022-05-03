package fun.fengwk.convention4j.example.concurrent;

import fun.fengwk.convention4j.common.concurrent.NamedThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
public class NamedThreadFactoryExample {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new NamedThreadFactory("示例线程"),
                new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }

}
