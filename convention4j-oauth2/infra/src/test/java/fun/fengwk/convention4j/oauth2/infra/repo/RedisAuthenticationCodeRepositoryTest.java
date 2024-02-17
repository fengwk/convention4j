package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.oauth2.infra.OAuth2InfraTestApplication;
import fun.fengwk.convention4j.oauth2.server.model.AuthenticationCode;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

/**
 * @author fengwk
 */
@SpringBootTest(classes = OAuth2InfraTestApplication.class)
@RunWith(SpringRunner.class)
public class RedisAuthenticationCodeRepositoryTest {

    @Autowired
    private RedisAuthenticationCodeRepository redisAuthenticationCodeRepository;

    @Test
    public void test() throws InterruptedException {
        AuthenticationCode authenticationCode = new AuthenticationCode();
        authenticationCode.setCode("code");
        authenticationCode.setSubjectId("sid");
        authenticationCode.setResponseType(ResponseType.CODE);
        authenticationCode.setClientId("cid");
        authenticationCode.setRedirectUri("https://fengwk.fun");
        authenticationCode.setScope("scope");

        assert redisAuthenticationCodeRepository.add(authenticationCode, 3);

        AuthenticationCode found = redisAuthenticationCodeRepository.get(authenticationCode.getCode());
        assert Objects.equals(found, authenticationCode);

        assert redisAuthenticationCodeRepository.add(authenticationCode, 1);
        Thread.sleep(1500L);
        assert redisAuthenticationCodeRepository.get(authenticationCode.getCode()) == null;
    }

}
