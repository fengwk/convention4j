package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.oauth2.core.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.infra.OAuth2InfraPresetTestApplication;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author fengwk
 */
@SpringBootTest(classes = OAuth2InfraPresetTestApplication.class)
@RunWith(SpringRunner.class)
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
        assert mysqlOAuth2TokenRepository.add(oauth2Token);

        OAuth2Token found = mysqlOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assert Objects.equals(found, oauth2Token);

        found = mysqlOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assert Objects.equals(found, oauth2Token);

        oauth2Token.setId(1L);
        oauth2Token.setClientId("cid_update");
        oauth2Token.setSubjectId("sid_update");
        oauth2Token.setScope("userInfoUpdate");
        oauth2Token.setTokenType(TokenType.BEARER);
        oauth2Token.setAccessToken("accessTokenUpdate");
        oauth2Token.setRefreshToken("refreshTokenUpdate");

        assert mysqlOAuth2TokenRepository.updateById(oauth2Token);

        found = mysqlOAuth2TokenRepository.getByAccessToken(oauth2Token.getAccessToken());
        assert Objects.equals(found, oauth2Token);

        found = mysqlOAuth2TokenRepository.getByRefreshToken(oauth2Token.getRefreshToken());
        assert Objects.equals(found, oauth2Token);
    }

}
