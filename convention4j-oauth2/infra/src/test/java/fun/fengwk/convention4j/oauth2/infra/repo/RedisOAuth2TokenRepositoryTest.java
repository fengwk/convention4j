package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.oauth2.infra.OAuth2InfraTestApplication;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fengwk
 */
@SpringBootTest(classes = OAuth2InfraTestApplication.class)
public class RedisOAuth2TokenRepositoryTest {

    @Autowired
    private RedisOAuth2TokenRepository redisOAuth2TokenRepository;

    @Test
    public void test() {
        OAuth2Token oauth2Token = new OAuth2Token();
        oauth2Token.setId(1L);
        oauth2Token.setClientId("cid");
        oauth2Token.setSubjectId("sid");
        oauth2Token.setScope("userInfo");
        oauth2Token.setTokenType(TokenType.BEARER);
        oauth2Token.setAccessToken("accessToken");
        oauth2Token.setRefreshToken("refreshToken");
        oauth2Token.setSsoId("ssoId");
        // nanoOfSecond必须使用0，否则写入h2再查询会丢失精度导致equals失败
        LocalDateTime time = LocalDateTime.now();
        oauth2Token.setLastRefreshTime(time);
        oauth2Token.setAuthorizeTime(time);
        assertTrue(redisOAuth2TokenRepository.add(oauth2Token, 60));

        OAuth2Token found = redisOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assertEquals(oauth2Token, found);

        found = redisOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assertEquals(oauth2Token, found);

        oauth2Token.setId(1L);
        oauth2Token.setClientId("cid_update");
        oauth2Token.setSubjectId("sid_update");
        oauth2Token.setScope("userInfoUpdate");
        oauth2Token.setTokenType(TokenType.BEARER);
        oauth2Token.setAccessToken("accessTokenUpdate");
        oauth2Token.setRefreshToken("refreshTokenUpdate");

        assertTrue(redisOAuth2TokenRepository.updateById(oauth2Token, 60));

        found = redisOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assertEquals(oauth2Token, found);

        found = redisOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assertEquals(oauth2Token, found);

        assertTrue(redisOAuth2TokenRepository.removeByAccessToken(oauth2Token.getAccessToken()));
        assertNull(redisOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken()));
        assertNull(redisOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken()));
        assertNull(redisOAuth2TokenRepository.getBySsoId(oauth2Token.getSsoId()));
    }

}
