package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;
import java.util.Objects;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;

/**
 * @author fengwk
 */
public class RedisTemplateScriptExecutor extends AbstractLifeCycle implements RedisScriptExecutor {

    private final RedisTemplate redisTemplate;

    /**
     *
     * @param redisTemplate not null
     */
    public RedisTemplateScriptExecutor(RedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
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
        RedisScript<T> redisScript = new DefaultRedisScript<>(script, returnType);
        @SuppressWarnings("unchecked")
        Object res = redisTemplate.execute(
                redisScript,
                RedisSerializer.string(),
                RedisSerializer.string(),
                keys,
                args.toArray());
        return returnType.cast(res);
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
        // nothing to do
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

}
