package fun.fengwk.convention4j.oauth2.core;

import fun.fengwk.convention4j.common.reflect.TypeResolver;
import fun.fengwk.convention4j.oauth2.core.controller.OAuth2Controller;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2ClientManager;
import fun.fengwk.convention4j.oauth2.core.manager.OAuth2SubjectManager;
import fun.fengwk.convention4j.oauth2.core.manager.Standard2ClientManager;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.context.TokenContext;
import fun.fengwk.convention4j.oauth2.core.repo.AuthenticationCodeRepository;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2ClientRepository;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2TokenRepository;
import fun.fengwk.convention4j.oauth2.core.service.OAuth2Service;
import fun.fengwk.convention4j.oauth2.core.service.OAuth2ServiceImpl;
import fun.fengwk.convention4j.oauth2.core.service.mode.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
@EnableConfigurationProperties(OAuth2Properties.class)
public abstract class OAuth2ConfigureTemplate<
    SUBJECT,
    CERTIFICATE,
    CLIENT extends OAuth2Client
    > {

    private static final int CERTIFICATE_GENERIC_INDEX = 1;

    @ConditionalOnMissingBean
    @Bean
    public OAuth2ClientManager oauth2ClientManager(OAuth2ClientRepository<CLIENT> oauth2ClientRepository) {
        return new Standard2ClientManager<>(oauth2ClientRepository);
    }

    @Bean
    public AuthenticationCodeMode<SUBJECT, CERTIFICATE> authenticationCodeMode(
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository,
        AuthenticationCodeRepository authenticationCodeRepository) {
        return new AuthenticationCodeMode<>(oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository, authenticationCodeRepository);
    }

    @Bean
    public ImplicitMode<SUBJECT, CERTIFICATE> implicitMode(
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository) {
        return new ImplicitMode<>(oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository);
    }

    @Bean
    public PasswordMode<SUBJECT, CERTIFICATE> passwordMode(
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository) {
        return new PasswordMode<>(oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository);
    }

    @Bean
    public ClientCredentialsMode<SUBJECT, CERTIFICATE> clientCredentialsMode(
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository) {
        return new ClientCredentialsMode<>(oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository);
    }

    @Bean
    public RefreshTokenService<SUBJECT, CERTIFICATE> refreshTokenMode(
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository) {
        return new RefreshTokenService<>(oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository);
    }

    @Bean
    public OAuth2Service<SUBJECT, CERTIFICATE> oauth2Service(
        List<OAuth2AuthorizeService<CERTIFICATE>> oauth2AuthorizeServices,
        List<OAuth2TokenService<? extends TokenContext>> oauth2TokenServices,
        OAuth2ClientManager oauth2ClientManager,
        OAuth2SubjectManager<SUBJECT, CERTIFICATE> oauth2SubjectManager,
        OAuth2TokenRepository oauth2TokenRepository) {
        return new OAuth2ServiceImpl<>(oauth2AuthorizeServices, oauth2TokenServices,
            oauth2ClientManager, oauth2SubjectManager, oauth2TokenRepository);
    }

    @Bean
    public OAuth2Controller<SUBJECT, CERTIFICATE> oauth2Controller(
        OAuth2Service<SUBJECT, CERTIFICATE> oauth2Service,
        OAuth2Properties oauth2Properties) {
        // TODO ResolvableType无法解析，获得null
//        ResolvableType rt = ResolvableType.forClass(getClass()).as(OAuth2CoreConfigureTemplate.class);
//        Type certificateType = rt.getGeneric(CERTIFICATE_GENERIC_INDEX).getType();
        TypeResolver tr = new TypeResolver(getClass()).as(OAuth2ConfigureTemplate.class);
        ParameterizedType pt = tr.asParameterizedType();
        Type certificateType = pt.getActualTypeArguments()[CERTIFICATE_GENERIC_INDEX];
        Objects.requireNonNull(certificateType, "certificateType can not be null");
        return new OAuth2Controller<>(oauth2Service, oauth2Properties, certificateType);
    }

}