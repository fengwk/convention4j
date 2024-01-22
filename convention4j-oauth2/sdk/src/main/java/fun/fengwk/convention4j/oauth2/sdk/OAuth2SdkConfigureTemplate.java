package fun.fengwk.convention4j.oauth2.sdk;

import fun.fengwk.convention4j.common.reflect.TypeResolver;
import fun.fengwk.convention4j.oauth2.sdk.aspect.AuthorizationAspect;
import fun.fengwk.convention4j.oauth2.sdk.client.*;
import fun.fengwk.convention4j.oauth2.sdk.client.impl.*;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.sdk.context.OAuth2Context;
import fun.fengwk.convention4j.oauth2.sdk.context.impl.ThreadLocalOAuth2Context;
import fun.fengwk.convention4j.oauth2.sdk.context.impl.ThreadLocalOAuth2ContextInterceptor;
import fun.fengwk.convention4j.oauth2.sdk.controller.SdkOAuth2Controller;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2TokenSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.manager.impl.RedisOAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.manager.impl.RedisOAuth2TokenSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.service.SdkOAuth2Service;
import fun.fengwk.convention4j.oauth2.sdk.service.impl.SdkOAuth2ServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
@EnableAspectJAutoProxy
@EnableConfigurationProperties(OAuth2SdkProperties.class)
public class OAuth2SdkConfigureTemplate<SUBJECT, CERTIFICATE> implements WebMvcConfigurer {

    @Bean
    public OAuth2HttpClientManager oauth2HttpClientManager(OAuth2SdkProperties oauth2SdkProperties) {
        return new OAuth2HttpClientManager(oauth2SdkProperties.getHttpClient());
    }

    @ConditionalOnMissingBean
    @Bean
    public AuthorizeClient<CERTIFICATE> authorizeHttpClient(OAuth2HttpClientManager oauth2HttpClientManager,
                                                            OAuth2SdkProperties oauth2SdkProperties) {
        return new AuthorizeHttpClient<>(oauth2HttpClientManager, oauth2SdkProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public TokenClient<CERTIFICATE> tokenClient(OAuth2HttpClientManager oauth2HttpClientManager,
                                                OAuth2SdkProperties oauth2SdkProperties) {
        return new TokenHttpClient<>(oauth2HttpClientManager, oauth2SdkProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public RefreshTokenClient refreshTokenClient(OAuth2HttpClientManager oauth2HttpClientManager,
                                                 OAuth2SdkProperties oauth2SdkProperties) {
        return new RefreshTokenHttpClient(oauth2HttpClientManager, oauth2SdkProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public SubjectClient<SUBJECT> subjectSubjectClient(OAuth2HttpClientManager oauth2HttpClientManager,
                                                       OAuth2SdkProperties oauth2SdkProperties) {
        TypeResolver tr = new TypeResolver(getClass()).as(OAuth2SdkConfigureTemplate.class);
        Type subjectType = tr.asParameterizedType().getActualTypeArguments()[0];
        return new SubjectHttpClient<>(oauth2HttpClientManager, oauth2SdkProperties, subjectType);
    }

    @ConditionalOnMissingBean
    @Bean
    public RevokeTokenClient revokeTokenClient(OAuth2HttpClientManager oauth2HttpClientManager,
                                               OAuth2SdkProperties oauth2SdkProperties) {
        return new RevokeTokenHttpClient(oauth2HttpClientManager, oauth2SdkProperties);
    }

    @Bean
    public OAuth2TokenSessionManager oauth2TokenSessionManager(StringRedisTemplate redisTemplate,
                                                               OAuth2SdkProperties oauth2SdkProperties) {
        return new RedisOAuth2TokenSessionManager(redisTemplate, oauth2SdkProperties.getTokenSession());
    }

    @Bean
    public OAuth2StateSessionManager oauth2StateSessionManager(StringRedisTemplate redisTemplate,
                                                               OAuth2SdkProperties oauth2SdkProperties) {
        return new RedisOAuth2StateSessionManager(redisTemplate, oauth2SdkProperties.getStateSession());
    }

    @Bean
    public SdkOAuth2Service sdkOAuth2Service(
        OAuth2TokenSessionManager oauth2TokenSessionManager,
        OAuth2StateSessionManager oauth2StateSessionManager,
        TokenClient<CERTIFICATE> tokenClient,
        RevokeTokenClient revokeTokenClient,
        OAuth2SdkProperties oauth2SdkProperties) {
        return new SdkOAuth2ServiceImpl<>(oauth2TokenSessionManager, oauth2StateSessionManager,
            tokenClient, revokeTokenClient, oauth2SdkProperties);
    }

    @Bean
    public ThreadLocalOAuth2Context<SUBJECT> threadLocalOAuth2Context(
        SubjectClient<SUBJECT> oauth2SubjectClient,
        RefreshTokenClient refreshTokenClient,
        OAuth2TokenSessionManager oauth2TokenSessionManager,
        OAuth2StateSessionManager oauth2StateSessionManager,
        OAuth2SdkProperties oauth2SdkProperties) {
        return new ThreadLocalOAuth2Context<>(oauth2SubjectClient, refreshTokenClient,
            oauth2TokenSessionManager, oauth2StateSessionManager, oauth2SdkProperties);
    }

    @Bean
    public ThreadLocalOAuth2ContextInterceptor<SUBJECT> threadLocalOAuth2ContextInterceptor() {
        return new ThreadLocalOAuth2ContextInterceptor<>();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(threadLocalOAuth2ContextInterceptor());
    }

    @Bean
    public AuthorizationAspect<SUBJECT> authorizationAspect(OAuth2Context<SUBJECT> oauth2Context) {
        return new AuthorizationAspect<>(oauth2Context);
    }

    @Bean
    public SdkOAuth2Controller<SUBJECT> sdkOAuth2Controller(
        OAuth2Context<SUBJECT> oauth2Context,
        SdkOAuth2Service sdkOAuth2TokenService,
        OAuth2StateSessionManager oauth2StateSessionManager,
        OAuth2SdkProperties oauth2SdkProperties) {
        return new SdkOAuth2Controller<>(oauth2Context, sdkOAuth2TokenService,
            oauth2StateSessionManager, oauth2SdkProperties);
    }

}
