package fun.fengwk.convention4j.oauth2.sdk.manager.impl;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.sdk.config.StateSessionConfig;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class RedisOAuth2StateSessionManager implements OAuth2StateSessionManager {

    private static final String REDIS_KEY_OAUTH2_STATE = "OAUTH2_STATE_SESSION:%s";

    private final StringRedisTemplate redisTemplate;
    private final StateSessionConfig stateSessionConfig;

    @Override
    public String generateState() {
        String state = UUID.randomUUID().toString().replace("-", "");
        String key = String.format(REDIS_KEY_OAUTH2_STATE, state);
        redisTemplate.opsForValue().set(key, "1", stateSessionConfig.getMaxStoreSeconds(), TimeUnit.SECONDS);
        return state;
    }

    @Override
    public boolean invalidState(String state) {
        String key = String.format(REDIS_KEY_OAUTH2_STATE, state);
        return NullSafe.of(redisTemplate.delete(key), false);
    }

    @Override
    public boolean verifyState(String state) {
        String key = String.format(REDIS_KEY_OAUTH2_STATE, state);
        return NullSafe.of(redisTemplate.hasKey(key), false);
    }

}
