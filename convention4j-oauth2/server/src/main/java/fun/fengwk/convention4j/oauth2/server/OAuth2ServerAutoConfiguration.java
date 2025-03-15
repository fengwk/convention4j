package fun.fengwk.convention4j.oauth2.server;

import fun.fengwk.convention4j.oauth2.server.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.server.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.server.service.OAuth2ServiceImpl;
import fun.fengwk.convention4j.oauth2.server.service.mode.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@ConditionalOnBean({ OAuth2ClientManager.class, OAuth2SubjectManager.class })
@Import({
    AuthenticationCodeMode.class,
    ImplicitMode.class,
    PasswordMode.class,
    ClientCredentialsMode.class,
    RefreshTokenService.class,
    OAuth2ServiceImpl.class })
@Configuration
public class OAuth2ServerAutoConfiguration {

}