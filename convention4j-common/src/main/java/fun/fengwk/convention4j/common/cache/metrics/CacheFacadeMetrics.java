package fun.fengwk.convention4j.common.cache.metrics;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author fengwk
 */
public class CacheFacadeMetrics implements CacheFacade {

    private final LongAdder writeCounter = new LongAdder();
    private final LongAdder readCounter = new LongAdder();
    private final LongAdder writeKeyCounter = new LongAdder();
    private final CacheFacade delegate;

    public CacheFacadeMetrics(CacheFacade delegate) {
        this.delegate = delegate;
    }

    @Override
    public void set(String key, String value, int expireSeconds) {
        writeCounter.increment();
        writeKeyCounter.increment();
        delegate.set(key, value, expireSeconds);
    }

    @Override
    public String get(String key) {
        readCounter.increment();
        return delegate.get(key);
    }

    @Override
    public void batchSet(Map<String, String> kvMap, int expireSeconds) {
        writeCounter.increment();
        writeKeyCounter.add(kvMap.size());
        delegate.batchSet(kvMap, expireSeconds);
    }

    @Override
    public Map<String, String> batchGet(Collection<String> keys) {
        readCounter.increment();
        return delegate.batchGet(keys);
    }

    @Override
    public void batchDelete(Collection<String> keys) {
        writeCounter.increment();
        writeKeyCounter.add(keys.size());
        delegate.batchDelete(keys);
    }

    public long getWriteCount() {
        return writeCounter.longValue();
    }

    public long getReadCount() {
        return readCounter.longValue();
    }

    public long getWriteKeyCount() {
        return writeKeyCounter.longValue();
    }

}
