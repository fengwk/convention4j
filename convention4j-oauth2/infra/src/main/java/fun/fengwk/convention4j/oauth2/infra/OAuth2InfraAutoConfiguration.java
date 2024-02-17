package fun.fengwk.convention4j.oauth2.infra;

import fun.fengwk.convention4j.oauth2.infra.repo.MysqlOAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.infra.repo.RedisAuthenticationCodeRepository;
import fun.fengwk.convention4j.oauth2.infra.repo.RedisOAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@ComponentScan
@Import(RedisAuthenticationCodeRepository.class)
@Configuration
public class OAuth2InfraAutoConfiguration {

    @ConditionalOnMissingBean(OAuth2TokenRepository.class)
    @ConditionalOnProperty(
        prefix = "convention.oauth2",
        name = "enable-mysql-repo",
        havingValue = "false",
        matchIfMissing = true)
    @Import(RedisOAuth2TokenRepository.class)
    static class RedisOAuth2TokenConfiguration {
    }

    @ConditionalOnMissingBean(OAuth2TokenRepository.class)
    @ConditionalOnProperty(
        prefix = "convention.oauth2",
        name = "enable-mysql-repo",
        havingValue = "true")
    @Import(MysqlOAuth2TokenRepository.class)
    @BaseMapperScan
    static class MysqlOAuth2TokenConfiguration {
    }

}
