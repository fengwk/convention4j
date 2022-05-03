package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

import java.util.List;
import java.util.Objects;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;

/**
 * @author fengwk
 */
public class JedisPoolScriptExecutor extends AbstractLifeCycle implements RedisScriptExecutor {

    private static final Logger log = LoggerFactory.getLogger(JedisPoolScriptExecutor.class);

    private final Pool<Jedis> pool;

    /**
     *
     * @param pool not null
     */
    public JedisPoolScriptExecutor(Pool<Jedis> pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) {
        getLifeCycleRwLock().readLock().lock();
        try {
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            return doExecute(script, keys, args, returnType);
        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    private <T> T doExecute(String script, List<String> keys, List<String> args, Class<T> returnType) {
        try (Jedis jedis = pool.getResource()) {
            Long begin = null;
            if (log.isDebugEnabled()) {
                begin = System.currentTimeMillis();
            }

            Object res = jedis.eval(script, keys, args);

            if (begin != null) {
                log.debug("eval cost {} ms", System.currentTimeMillis() - begin);
            }

            return returnType.cast(res);
        }
    }

    @Override
    protected void doInit() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStart() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStop() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doClose() throws LifeCycleException {
        if (!pool.isClosed()) {
            pool.close();
        }
    }

    @Override
    protected void doFail() throws LifeCycleException {
        if (!pool.isClosed()) {
            pool.close();
        }
    }

}
