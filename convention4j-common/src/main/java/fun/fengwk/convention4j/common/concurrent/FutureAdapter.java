package fun.fengwk.convention4j.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 允许已得到的值适配{@link Future}接口。
 *
 * @author fengwk
 */
public class FutureAdapter<V> implements Future<V> {

    private final V val;

    public FutureAdapter(V val) {
        this.val = val;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return val;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return val;
    }

}
