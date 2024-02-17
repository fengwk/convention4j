package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class RedisOAuth2TokenRepository implements OAuth2TokenRepository {

    private static final String REDIS_KEY_OAUTH2_TOKEN = "OAUTH2_TOKEN:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX = "OAUTH2_TOKEN_AT:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX = "OAUTH2_TOKEN_RT:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX = "OAUTH2_TOKEN_SI:%s";

    private final NamespaceIdGenerator<Long> idGenerator;
    private final StringRedisTemplate redisTemplate;

    @Override
    public long generateId() {
        return idGenerator.next(OAuth2Token.class);
    }

    @Override
    public boolean add(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        int authorizeExpiresIn = oauth2Token.authorizeExpiresIn(authorizeExpireSeconds);
        if (authorizeExpiresIn <= 0) {
            return false;
        }
        String tokenKey = String.format(REDIS_KEY_OAUTH2_TOKEN, oauth2Token.getId());
        String atKey = String.format(REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX, oauth2Token.getAccessToken());
        String rtKey = String.format(REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX, oauth2Token.getRefreshToken());
        String siKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX, oauth2Token.getSsoId());
        String idValue = String.valueOf(oauth2Token.getId());
        String tokenValue = JsonUtils.toJson(oauth2Token);
        redisTemplate.opsForValue().set(atKey, idValue, authorizeExpiresIn, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(rtKey, idValue, authorizeExpiresIn, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(siKey, idValue, authorizeExpiresIn, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(tokenKey, tokenValue, authorizeExpiresIn, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean updateById(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        // 获取旧的token
        String tokenKey = String.format(REDIS_KEY_OAUTH2_TOKEN, oauth2Token.getId());
        String oldTokenValue = redisTemplate.opsForValue().get(tokenKey);
        if (StringUtils.isBlank(oldTokenValue)) {
            return false;
        }

        // 删除旧的索引
        OAuth2Token oldToken = JsonUtils.fromJson(oldTokenValue, OAuth2Token.class);
        String oldAtKey = String.format(REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX, oldToken.getAccessToken());
        String oldRtKey = String.format(REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX, oldToken.getRefreshToken());
        String oldSiKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX, oldToken.getSsoId());
        redisTemplate.delete(oldAtKey);
        redisTemplate.delete(oldRtKey);
        redisTemplate.delete(oldSiKey);

        // 重新添加令牌
        return add(oauth2Token, authorizeExpireSeconds);
    }

    @Override
    public boolean removeByAccessToken(String accessToken) {
        String atKey = String.format(REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX, accessToken);
        String idValue = redisTemplate.opsForValue().get(atKey);
        if (StringUtils.isBlank(idValue)) {
            return false;
        }

        long id = NumberUtils.toLong(idValue);
        String tokenKey = String.format(REDIS_KEY_OAUTH2_TOKEN, id);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        if (StringUtils.isBlank(tokenValue)) {
            return false;
        }

        OAuth2Token token = JsonUtils.fromJson(tokenValue, OAuth2Token.class);
        String rtKey = String.format(REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX, token.getRefreshToken());
        String siKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX, token.getSsoId());
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(atKey);
        redisTemplate.delete(rtKey);
        redisTemplate.delete(siKey);
        return true;
    }

    @Override
    public OAuth2Token getByAccessToken(String accessToken) {
        String atKey = String.format(REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX, accessToken);
        String idValue = redisTemplate.opsForValue().get(atKey);
        if (StringUtils.isBlank(idValue)) {
            return null;
        }
        return getByIdValue(idValue);
    }

    @Override
    public OAuth2Token getByRefreshToken(String refreshToken) {
        String rtKey = String.format(REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX, refreshToken);
        String idValue = redisTemplate.opsForValue().get(rtKey);
        if (StringUtils.isBlank(idValue)) {
            return null;
        }
        return getByIdValue(idValue);
    }

    @Override
    public OAuth2Token getBySsoId(String ssoId) {
        String siKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX, ssoId);
        String idValue = redisTemplate.opsForValue().get(siKey);
        if (StringUtils.isBlank(idValue)) {
            return null;
        }
        return getByIdValue(idValue);
    }

    private OAuth2Token getByIdValue(String idValue) {
        long id = NumberUtils.toLong(idValue);
        String tokenKey = String.format(REDIS_KEY_OAUTH2_TOKEN, id);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        if (StringUtils.isBlank(tokenValue)) {
            return null;
        }
        return JsonUtils.fromJson(tokenValue, OAuth2Token.class);
    }

}
