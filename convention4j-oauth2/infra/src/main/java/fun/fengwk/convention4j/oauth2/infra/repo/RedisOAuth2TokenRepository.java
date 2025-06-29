package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.CollectionUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class RedisOAuth2TokenRepository implements OAuth2TokenRepository {

    private static final String REDIS_KEY_OAUTH2_TOKEN = "OAUTH2_TOKEN:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_ACCESS_TOKEN_INDEX = "OAUTH2_TOKEN_AT:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_REFRESH_TOKEN_INDEX = "OAUTH2_TOKEN_RT:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX = "OAUTH2_TOKEN_SI:%s";
    private static final String REDIS_KEY_OAUTH2_TOKEN_SUBJECT_ID_INDEX = "OAUTH2_TOKEN_SUB:%s";

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
        String subKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SUBJECT_ID_INDEX, oauth2Token.getSubjectId());
        String idValue = String.valueOf(oauth2Token.getId());
        String tokenValue = JsonUtils.toJson(oauth2Token);

        // accessToken->tokenId
        redisTemplate.opsForValue().set(atKey, idValue, authorizeExpiresIn, TimeUnit.SECONDS);
        // refreshToken->tokenId
        redisTemplate.opsForValue().set(rtKey, idValue, authorizeExpiresIn, TimeUnit.SECONDS);
        // ssoId->tokenId
        long siExpire = addToSet(siKey, idValue, authorizeExpiresIn);
        // subjectId->tokenId
        long subExpire = addToSet(subKey, idValue, authorizeExpiresIn);
        // tokenId->token
        redisTemplate.opsForValue().set(tokenKey, tokenValue, authorizeExpiresIn, TimeUnit.SECONDS);

        log.debug("Add oauth2 token, set accessToken->tokenId, atKey: {}, idValue: {} authorizeExpiresIn: {}s",
            atKey, idValue, authorizeExpireSeconds);
        log.debug("Add oauth2 token, set refreshToken->tokenId, rtKey: {}, idValue: {} authorizeExpiresIn: {}s",
            rtKey, idValue, authorizeExpireSeconds);
        log.debug("Add oauth2 token, set ssoId->tokenId, siKey: {}, idValue: {} siExpire: {}s",
            siKey, idValue, siExpire);
        log.debug("Add oauth2 token, set subjectId->tokenId, subKey: {}, idValue: {} subExpire: {}s",
            subKey, idValue, subExpire);
        log.debug("Add oauth2 token, set tokenId->token, tokenKey: {}, tokenValue: {} authorizeExpiresIn: {}s",
            tokenKey, tokenValue, authorizeExpireSeconds);
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
        String oldSubKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SUBJECT_ID_INDEX, oldToken.getSubjectId());
        redisTemplate.delete(oldAtKey);
        redisTemplate.delete(oldRtKey);
        redisTemplate.opsForSet().remove(oldSiKey, String.valueOf(oldToken.getId()));
        redisTemplate.opsForSet().remove(oldSubKey, String.valueOf(oldToken.getId()));
        log.debug("Update oauth2 token, delete accessToken->tokenId, atKey: {}", oldAtKey);
        log.debug("Update oauth2 token, delete refreshToken->tokenId, rtToken: {}", oldRtKey);
        log.debug("Update oauth2 token, remove ssoId->tokenId, siKey: {}", oldSiKey);
        log.debug("Update oauth2 token, remove subjectId->tokenId, subKey: {}", oldSubKey);

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
        String subKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SUBJECT_ID_INDEX, token.getSubjectId());
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(atKey);
        redisTemplate.delete(rtKey);
        redisTemplate.opsForSet().remove(siKey, String.valueOf(token.getId()));
        redisTemplate.opsForSet().remove(subKey, String.valueOf(token.getId()));
        log.debug("Remove oauth2 token, delete tokenId->token, tokenKey: {}", tokenKey);
        log.debug("Remove oauth2 token, delete accessToken->tokenId, atKey: {}", atKey);
        log.debug("Remove oauth2 token, delete refreshToken->tokenId, rtKey: {}", rtKey);
        log.debug("Remove oauth2 token, delete ssoId->tokenId, siKey: {}", siKey);
        log.debug("Remove oauth2 token, remove subjectId->tokenId, subKey: {}", subKey);
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
    public OAuth2Token getBySsoIdAndSsoDomain(String ssoId, String ssoDomain) {
        for (OAuth2Token oauth2Token : listBySsoId(ssoId)) {
            if (Objects.equals(oauth2Token.getSsoDomain(), ssoDomain)) {
                return oauth2Token;
            }
        }
        return null;
    }

    @Override
    public List<OAuth2Token> listBySsoId(String ssoId) {
        String siKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SSO_ID_INDEX, ssoId);
        Set<String> tokenIds = redisTemplate.opsForSet().members(siKey);
        return getByTokenIds(tokenIds);
    }

    @Override
    public List<OAuth2Token> listBySubjectId(String subjectId) {
        String subKey = String.format(REDIS_KEY_OAUTH2_TOKEN_SUBJECT_ID_INDEX, subjectId);
        Set<String> tokenIds = redisTemplate.opsForSet().members(subKey);
        return getByTokenIds(tokenIds);
    }

    private List<OAuth2Token> getByTokenIds(Set<String> tokenIds) {
        if (CollectionUtils.isEmpty(tokenIds)) {
            return Collections.emptyList();
        }
        List<String> tokenKeys = NullSafe.of(tokenIds).stream()
            .map(tid -> String.format(REDIS_KEY_OAUTH2_TOKEN, tid))
            .collect(Collectors.toList());
        List<String> tokenValues = redisTemplate.opsForValue().multiGet(tokenKeys);
        return NullSafe.of(tokenValues).stream()
            .map(v -> JsonUtils.fromJson(v, OAuth2Token.class))
            .collect(Collectors.toList());
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

    private long addToSet(String setKey, String value, int valueExpire) {
        redisTemplate.opsForSet().add(setKey, value);
        long expire = redisTemplate.getExpire(setKey, TimeUnit.SECONDS);
        expire = Math.max(expire, valueExpire);
        redisTemplate.expire(setKey, Duration.ofSeconds(expire));
        return expire;
    }

}
