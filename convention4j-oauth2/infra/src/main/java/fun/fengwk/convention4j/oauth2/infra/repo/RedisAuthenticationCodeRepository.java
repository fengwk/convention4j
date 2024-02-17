package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.oauth2.server.model.AuthenticationCode;
import fun.fengwk.convention4j.oauth2.server.repo.AuthenticationCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
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
        redisTemplate.opsForValue().set(key, serializedStr,expireSeconds, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean remove(String code) {
        String key = String.format(REDIS_KEY_OAUTH2_CODE, code);
        return NullSafe.of(redisTemplate.delete(key), false);
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
