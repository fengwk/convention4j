package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.oauth2.infra.OAuth2InfraTestApplication;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fengwk
 */
@SpringBootTest(classes = OAuth2InfraTestApplication.class)
public class MysqlOAuth2TokenRepositoryTest {

    @Autowired
    private MysqlOAuth2TokenRepository mysqlOAuth2TokenRepository;

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
        LocalDateTime time = LocalDateTime.of(2024, 1, 23, 12, 0, 0, 0);
        oauth2Token.setLastRefreshTime(time);
        oauth2Token.setAuthorizeTime(time);
        assertTrue(mysqlOAuth2TokenRepository.add(oauth2Token, 0));

        OAuth2Token found = mysqlOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assertEquals(oauth2Token, found);

        found = mysqlOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assertEquals(oauth2Token, found);

        oauth2Token.setId(1L);
        oauth2Token.setClientId("cid_update");
        oauth2Token.setSubjectId("sid_update");
        oauth2Token.setScope("userInfoUpdate");
        oauth2Token.setTokenType(TokenType.BEARER);
        oauth2Token.setAccessToken("accessTokenUpdate");
        oauth2Token.setRefreshToken("refreshTokenUpdate");

        assertTrue(mysqlOAuth2TokenRepository.updateById(oauth2Token, 0));

        found = mysqlOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assertEquals(oauth2Token, found);

        found = mysqlOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assertEquals(oauth2Token, found);

        List<OAuth2Token> oAuth2Tokens = mysqlOAuth2TokenRepository.listBySubjectId(oauth2Token.getSubjectId());
        assertEquals(1, oAuth2Tokens.size());
        assertEquals(oauth2Token, oAuth2Tokens.get(0));

        assertTrue(mysqlOAuth2TokenRepository.removeByAccessToken(oauth2Token.getAccessToken()));
        assertNull(mysqlOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken()));
        assertNull(mysqlOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken()));
        assertNull(mysqlOAuth2TokenRepository.getBySsoId(oauth2Token.getSsoId()));
        oAuth2Tokens = mysqlOAuth2TokenRepository.listBySubjectId(oauth2Token.getSubjectId());
        assertEquals(0, oAuth2Tokens.size());
    }

}
