package fun.fengwk.convention4j.springboot.starter.cache.adapter;

import fun.fengwk.convention4j.common.function.VoidFunc0;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

/**
 * 事务场景下不进行缓存读写，只进行缓存失效，并在事务提交后进行缓存处理。
 *
 * <p>事务场景下读写的问题</p>
 * <table>
 * <tr><td>事务A</td><td>事务B</td></tr>
 * <tr><td>x0->w->x1A</td><td>r->x0B</td></tr>
 * <tr><td>commit</td><td>commit</td></tr>
 * <tr><td>t->x0</td><td></td></tr>
 * </table>
 *
 * @author fengwk
 */
public class TransactionCacheAdapter implements CacheAdapter, TransactionSynchronization {

    private final ThreadLocal<LinkedList<LinkedList<VoidFunc0>>> commandQueueTL = ThreadLocal.withInitial(LinkedList::new);
    private final CacheAdapter delegate;

    public TransactionCacheAdapter(CacheAdapter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void set(String key, String value, int expireSeconds) {
        if (inTransaction()) {
            return;
        }
        delegate.set(key, value, expireSeconds);
    }

    @Override
    public String get(String key) {
        if (inTransaction()) {
            return null;
        }
        return delegate.get(key);
    }

    @Override
    public void batchSet(Map<String, String> kvMap, int expireSeconds) {
        if (kvMap == null || kvMap.isEmpty() || expireSeconds <= 0) {
            return;
        }
        if (inTransaction()) {
            return;
        }
        delegate.batchSet(kvMap, expireSeconds);
    }

    @Override
    public Map<String, String> batchGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        if (inTransaction()) {
            return Collections.emptyMap();
        }
        return delegate.batchGet(keys);
    }

    @Override
    public void batchDelete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        if (inTransaction()) {
            addTransactionCommand(() -> delegate.batchDelete(keys));
        } else {
            delegate.batchDelete(keys);
        }
    }

    @Override
    public void suspend() {
        LinkedList<LinkedList<VoidFunc0>> commandQueueStack = commandQueueTL.get();
        commandQueueStack.push(new LinkedList<>());
    }

    @Override
    public void afterCompletion(int status) {
        LinkedList<LinkedList<VoidFunc0>> commandQueueStack = commandQueueTL.get();
        try {
            if (status == STATUS_COMMITTED) {
                LinkedList<VoidFunc0> topCommandQueue = commandQueueStack.peek();
                while (!topCommandQueue.isEmpty()) {
                    VoidFunc0 cmd = topCommandQueue.removeFirst();
                    cmd.apply();
                }
            }
        } finally {
            commandQueueStack.pop();
        }
    }

    public void addTransactionCommand(VoidFunc0 command) {
        if (!TransactionSynchronizationManager.getSynchronizations().contains(this)) {
            TransactionSynchronizationManager.registerSynchronization(this);
        }
        LinkedList<LinkedList<VoidFunc0>> commandQueueStack = commandQueueTL.get();
        if (commandQueueStack.isEmpty()) {
            commandQueueStack.push(new LinkedList<>());
        }
        LinkedList<VoidFunc0> topCommandQueue = commandQueueStack.peek();
        topCommandQueue.addLast(command);
    }

    private boolean inTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

}
