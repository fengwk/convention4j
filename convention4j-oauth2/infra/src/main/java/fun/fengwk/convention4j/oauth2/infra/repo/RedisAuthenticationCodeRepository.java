package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.model.AuthenticationCode;
import fun.fengwk.convention4j.oauth2.server.repo.AuthenticationCodeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class RedisAuthenticationCodeRepository implements AuthenticationCodeRepository {

    private static final String REDIS_KEY_OAUTH2_CODE = "OAUTH2_CODE:%s";

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean add(AuthenticationCode authenticationCode, int expireSeconds) {
        if (authenticationCode == null) {
            return false;
        }
        String key = String.format(REDIS_KEY_OAUTH2_CODE, authenticationCode.getCode());
        String serializedStr = serialize(authenticationCode);
        redisTemplate.opsForValue().set(key, serializedStr, expireSeconds, TimeUnit.SECONDS);
        log.debug("Add authentication code, key: {}, value: {}, expireSeconds: {}s",
            key, serializedStr, expireSeconds);
        return true;
    }

    @Override
    public boolean remove(String code) {
        String key = String.format(REDIS_KEY_OAUTH2_CODE, code);
        Boolean result = redisTemplate.delete(key);
        log.debug("Remove authentication code, key: {}, result: {}", key, result);
        return NullSafe.of(result, false);
    }

    @Override
    public AuthenticationCode get(String code) {
        String key = String.format(REDIS_KEY_OAUTH2_CODE, code);
        String serializedStr = redisTemplate.opsForValue().get(key);
        return deSerialize(serializedStr);
    }

    public String serialize(AuthenticationCode authenticationCode) {
        return JsonUtils.toJson(authenticationCode);
    }

    public AuthenticationCode deSerialize(String serializedStr) {
        return JsonUtils.fromJson(serializedStr, AuthenticationCode.class);
    }

}
