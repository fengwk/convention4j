package fun.fengwk.convention4j.common.idgen.snowflakes;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class RedisTemplateScriptExecutor implements RedisScriptExecutor {

    private final RedisTemplate redisTemplate;

    /**
     *
     * @param redisTemplate not null
     */
    public RedisTemplateScriptExecutor(RedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public void close() throws Exception {
        // nothing to do
    }

    @Override
    public <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) throws Exception {
        RedisScript<T> redisScript = new DefaultRedisScript<>(script, returnType);
        Object res = redisTemplate.execute(
                redisScript,
                RedisSerializer.string(),
                RedisSerializer.string(),
                keys,
                args.toArray());
        return returnType.cast(res);
    }
}
