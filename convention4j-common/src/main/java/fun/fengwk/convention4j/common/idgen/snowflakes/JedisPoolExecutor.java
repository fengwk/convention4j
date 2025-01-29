package fun.fengwk.convention4j.common.idgen.snowflakes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisNoScriptException;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.Pool;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwk
 */
public class JedisPoolExecutor implements RedisExecutor {

    private static final Logger log = LoggerFactory.getLogger(JedisPoolExecutor.class);

    private static final Map<String, String> SCRIPT_SHA_MAP = new ConcurrentHashMap<>();

    private final Pool<Jedis> pool;

    /**
     *
     * @param pool not null
     */
    public JedisPoolExecutor(Pool<Jedis> pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) {
        try (Jedis jedis = pool.getResource()) {
            Long begin = null;
            if (log.isDebugEnabled()) {
                begin = System.currentTimeMillis();
            }

            String sha = loadScriptSha(jedis, script, null);
            Object res;
            try {
                res = jedis.evalsha(sha, keys, args);
            } catch (JedisNoScriptException ex) {
                // 重试一次应对脚本被清理的情况
                sha = loadScriptSha(jedis, script, sha);
                res = jedis.evalsha(sha, keys, args);
            }

            if (begin != null) {
                log.debug("eval cost {} ms", System.currentTimeMillis() - begin);
            }

            return returnType.cast(res);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public boolean setIfAbsent(String key, String value, long timeoutMs) {
        try (Jedis jedis = pool.getResource()) {
            SetParams setParams = new SetParams();
            setParams.nx();
            setParams.px(timeoutMs);
            return "OK".equals(jedis.set(key, value, setParams));
        }
    }

    @Override
    public boolean expire(String key, long timeoutMs) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.pexpire(key, timeoutMs) == 1L;
        }
    }

    private static String loadScriptSha(Jedis jedis, String script, String failedSha) {
        String sha = SCRIPT_SHA_MAP.get(script);
        if (sha != null && !Objects.equals(sha, failedSha)) {
            return sha;
        }
        return SCRIPT_SHA_MAP.compute(script, (sc, sh) -> {
           if (sh == null || Objects.equals(sh, failedSha)) {
               return jedis.scriptLoad(sc);
           }
           return sh;
        });
    }

}
