package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.util.NullSafe;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
public class RedisTemplateExecutor implements RedisExecutor {

    private final StringRedisTemplate redisTemplate;

    /**
     *
     * @param redisTemplate not null
     */
    public RedisTemplateExecutor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) {
        RedisScript<T> redisScript = new DefaultRedisScript<>(script, returnType);
        Object res = redisTemplate.execute(
            redisScript,
            keys,
            args.toArray());
        return returnType.cast(res);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean setIfAbsent(String key, String value, long timeoutMs) {
        Boolean res = redisTemplate.opsForValue().setIfAbsent(key, value, timeoutMs, TimeUnit.MILLISECONDS);
        return NullSafe.of(res);
    }

    @Override
    public boolean expire(String key, long timeoutMs) {
        Boolean res = redisTemplate.expire(key, timeoutMs, TimeUnit.MILLISECONDS);
        return NullSafe.of(res);
    }

}
