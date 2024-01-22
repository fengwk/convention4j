package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.oauth2.core.model.AuthenticationCode;
import fun.fengwk.convention4j.oauth2.core.repo.AuthenticationCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Repository
public class RedisAuthenticationCodeRepository implements AuthenticationCodeRepository {

    private static final String REDIS_KEY_AUTH_CODE = "AUTH_CODE:%s";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean add(AuthenticationCode authenticationCode, int expireSeconds) {
        if (authenticationCode == null) {
            return false;
        }
        String key = String.format(REDIS_KEY_AUTH_CODE, authenticationCode.getCode());
        String serializedStr = serialize(authenticationCode);
        stringRedisTemplate.opsForValue().set(key, serializedStr,expireSeconds, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean remove(String code) {
        String key = String.format(REDIS_KEY_AUTH_CODE, code);
        return NullSafe.of(stringRedisTemplate.delete(key), false);
    }

    @Override
    public AuthenticationCode get(String code) {
        String key = String.format(REDIS_KEY_AUTH_CODE, code);
        String serializedStr = stringRedisTemplate.opsForValue().get(key);
        return deSerialize(serializedStr);
    }

    public String serialize(AuthenticationCode authenticationCode) {
        return GsonUtils.toJson(authenticationCode);
    }

    public AuthenticationCode deSerialize(String serializedStr) {
        return GsonUtils.fromJson(serializedStr, AuthenticationCode.class);
    }

}
