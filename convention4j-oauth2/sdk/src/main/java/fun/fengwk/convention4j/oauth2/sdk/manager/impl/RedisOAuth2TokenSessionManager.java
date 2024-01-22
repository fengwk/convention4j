package fun.fengwk.convention4j.oauth2.sdk.manager.impl;

import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.oauth2.sdk.config.TokenSessionConfig;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2TokenSessionManager;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class RedisOAuth2TokenSessionManager implements OAuth2TokenSessionManager {

    private static final String REDIS_KEY_OAUTH2_TOKEN_SESSION = "OAUTH2_TOKEN_SESSION:%s";

    private final StringRedisTemplate redisTemplate;
    private final TokenSessionConfig tokenSessionConfig;

    @Override
    public void add(OAuth2TokenDTO tokenDTO) {
        if (tokenDTO != null) {
            String key = String.format(REDIS_KEY_OAUTH2_TOKEN_SESSION, tokenDTO.getAccessToken());
            redisTemplate.opsForValue().set(key, GsonUtils.toJson(tokenDTO),
                tokenSessionConfig.getMaxStoreSeconds(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void remove(String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            String key = String.format(REDIS_KEY_OAUTH2_TOKEN_SESSION, accessToken);
            redisTemplate.delete(key);
        }
    }

    @Override
    public OAuth2TokenDTO get(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        String key = String.format(REDIS_KEY_OAUTH2_TOKEN_SESSION, accessToken);
        String tokenDTOJson = redisTemplate.opsForValue().get(key);
        return GsonUtils.fromJson(tokenDTOJson, OAuth2TokenDTO.class);
    }

}
