package fun.fengwk.convention4j.oauth2.server.service;

import fun.fengwk.convention4j.api.code.ThrowableErrorCode;
import fun.fengwk.convention4j.oauth2.server.OAuth2CoreTestApplication;
import fun.fengwk.convention4j.oauth2.server.constant.TestConstants;
import fun.fengwk.convention4j.oauth2.server.model.User;
import fun.fengwk.convention4j.oauth2.server.model.UserCertificate;
import fun.fengwk.convention4j.oauth2.server.model.context.DefaultAuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.model.context.DefaultTokenContext;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fengwk
 */
@SpringBootTest(classes = OAuth2CoreTestApplication.class)
public class OAuth2ServiceTest {

    @Autowired
    private OAuth2Service<User, UserCertificate> oauth2Service;

    @Test
    public void testAuthenticationMode() {
        String state = "123";
        UserCertificate certificate = new UserCertificate();
        certificate.setUsername(TestConstants.USER1_USERNAME);
        certificate.setPassword(TestConstants.USER1_PASSWORD);
        DefaultAuthorizeContext<UserCertificate> authorizeContext = new DefaultAuthorizeContext<>();
        authorizeContext.setResponseType(ResponseType.CODE.getCode());
        authorizeContext.setClientId(TestConstants.CLIENT1_ID);
        authorizeContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        authorizeContext.setScope(TestConstants.CLIENT1_SCOPE);
        authorizeContext.setState(state);
        authorizeContext.setCertificate(certificate);
        URI uri = oauth2Service.authorize(authorizeContext);
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        assert Objects.equals(uriComponents.getQueryParams().getFirst("state"), state);
        String code = uriComponents.getQueryParams().getFirst("code");

        DefaultTokenContext<?> tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.AUTHORIZATION_CODE.getCode());
        tokenContext.setCode(code);
        tokenContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        tokenContext.setClientId(TestConstants.CLIENT1_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
        tokenContext.setScope(TestConstants.CLIENT1_SCOPE);
        OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(tokenContext);
        assert oauth2TokenDTO != null;
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getAccessToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getScope());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getTokenType());
        assert oauth2TokenDTO.getExpiresIn() > 0;

        tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.REFRESH_TOKEN.getCode());
        tokenContext.setClientId(TestConstants.CLIENT1_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
        tokenContext.setRefreshToken(oauth2TokenDTO.getRefreshToken());
        OAuth2TokenDTO refreshedDTO = oauth2Service.token(tokenContext);
        assert refreshedDTO != null;
        assert StringUtils.isNotEmpty(refreshedDTO.getAccessToken());
        assert StringUtils.isNotEmpty(refreshedDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(refreshedDTO.getScope());
        assert StringUtils.isNotEmpty(refreshedDTO.getTokenType());
        assert refreshedDTO.getExpiresIn() > 0;
        assert !Objects.equals(refreshedDTO.getAccessToken(), oauth2TokenDTO.getAccessToken());
        assert !Objects.equals(refreshedDTO.getRefreshToken(), oauth2TokenDTO.getRefreshToken());
    }

    @Test
    public void testImplicitMode() {
        String state = "123";
        UserCertificate certificate = new UserCertificate();
        certificate.setUsername(TestConstants.USER1_USERNAME);
        certificate.setPassword(TestConstants.USER1_PASSWORD);
        DefaultAuthorizeContext<UserCertificate> authorizeContext = new DefaultAuthorizeContext<>();
        authorizeContext.setResponseType(ResponseType.TOKEN.getCode());
        authorizeContext.setClientId(TestConstants.CLIENT2_ID);
        authorizeContext.setRedirectUri(TestConstants.CLIENT2_REDIRECT_URI);
        authorizeContext.setScope(TestConstants.CLIENT2_SCOPE);
        authorizeContext.setState(state);
        authorizeContext.setCertificate(certificate);
        URI uri = oauth2Service.authorize(authorizeContext);
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        assert Objects.equals(uriComponents.getQueryParams().getFirst("state"), state);
        assert NumberUtils.toInt(uriComponents.getQueryParams().getFirst("expiresIn")) > 0;
        assert Objects.equals(uriComponents.getQueryParams().getFirst("tokenType"), TokenType.BEARER.getCode());
        assert StringUtils.isNotEmpty(uriComponents.getQueryParams().getFirst("accessToken"));
    }

    @Test
    public void testPasswordMode() {
        UserCertificate certificate = new UserCertificate();
        certificate.setUsername(TestConstants.USER2_USERNAME);
        certificate.setPassword(TestConstants.USER2_PASSWORD);
        DefaultTokenContext<UserCertificate> tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.PASSWORD.getCode());
        tokenContext.setClientId(TestConstants.CLIENT2_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT2_SECRET);
        tokenContext.setScope(TestConstants.CLIENT2_SCOPE);
        tokenContext.setCertificate(certificate);
        OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(tokenContext);
        assert oauth2TokenDTO != null;
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getAccessToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getScope());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getTokenType());
        assert oauth2TokenDTO.getExpiresIn() > 0;
    }

    @Test
    public void testClientCredentialsMode() {
        DefaultTokenContext<?> tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.CLIENT_CREDENTIALS.getCode());
        tokenContext.setClientId(TestConstants.CLIENT2_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT2_SECRET);
        tokenContext.setScope(TestConstants.CLIENT2_SCOPE);
        tokenContext.setSubjectId(String.valueOf(TestConstants.USER1_ID));
        OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(tokenContext);
        assert oauth2TokenDTO != null;
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getAccessToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getScope());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getTokenType());
        assert oauth2TokenDTO.getExpiresIn() > 0;
    }

    @Test
    public void testAuthenticationMode_InvalidPassword() {
        assertThrows(ThrowableErrorCode.class, () -> {
            UserCertificate certificate = new UserCertificate();
            certificate.setUsername(TestConstants.USER1_USERNAME);
            certificate.setPassword("error password");
            DefaultAuthorizeContext<UserCertificate> authorizeContext = new DefaultAuthorizeContext<>();
            authorizeContext.setResponseType(ResponseType.CODE.getCode());
            authorizeContext.setClientId(TestConstants.CLIENT1_ID);
            authorizeContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
            authorizeContext.setScope(TestConstants.CLIENT1_SCOPE);
            authorizeContext.setState("123");
            authorizeContext.setCertificate(certificate);
            oauth2Service.authorize(authorizeContext);
        });
    }

    @Test
    public void testAuthenticationMode_InvalidCode() {
        assertThrows(ThrowableErrorCode.class, () -> {
            DefaultTokenContext<?> tokenContext = new DefaultTokenContext<>();
            tokenContext.setGrantType(GrantType.AUTHORIZATION_CODE.getCode());
            tokenContext.setCode("123");
            tokenContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
            tokenContext.setClientId(TestConstants.CLIENT1_ID);
            tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
            tokenContext.setScope(TestConstants.CLIENT1_SCOPE);
            oauth2Service.token(tokenContext);
        });
    }

    @Test
    public void testClientCredentialsMode_InvalidClientSecret() {
        assertThrows(ThrowableErrorCode.class, () -> {
            DefaultTokenContext<?> tokenContext = new DefaultTokenContext<>();
            tokenContext.setGrantType(GrantType.CLIENT_CREDENTIALS.getCode());
            tokenContext.setClientId(TestConstants.CLIENT2_ID);
            tokenContext.setClientSecret("error client secret");
            tokenContext.setScope(TestConstants.CLIENT2_SCOPE);
            tokenContext.setSubjectId(String.valueOf(TestConstants.USER1_ID));
            oauth2Service.token(tokenContext);
        });
    }

    @Test
    public void testAuthenticationMode_SSO() {
        String state = "123";
        UserCertificate certificate = new UserCertificate();
        certificate.setUsername(TestConstants.USER1_USERNAME);
        certificate.setPassword(TestConstants.USER1_PASSWORD);
        DefaultAuthorizeContext<UserCertificate> authorizeContext = new DefaultAuthorizeContext<>();
        authorizeContext.setResponseType(ResponseType.CODE.getCode());
        authorizeContext.setClientId(TestConstants.CLIENT1_ID);
        authorizeContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        authorizeContext.setScope(TestConstants.CLIENT1_SCOPE);
        authorizeContext.setState(state);
        authorizeContext.setCertificate(certificate);
        URI uri = oauth2Service.authorize(authorizeContext);
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        assert Objects.equals(uriComponents.getQueryParams().getFirst("state"), state);
        String code = uriComponents.getQueryParams().getFirst("code");
        String ssoId = authorizeContext.getSsoId();
        assert ssoId != null;

        DefaultTokenContext<?> tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.AUTHORIZATION_CODE.getCode());
        tokenContext.setCode(code);
        tokenContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        tokenContext.setClientId(TestConstants.CLIENT1_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
        tokenContext.setScope(TestConstants.CLIENT1_SCOPE);
        OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(tokenContext);
        assert oauth2TokenDTO != null;
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getAccessToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getScope());
        assert StringUtils.isNotEmpty(oauth2TokenDTO.getTokenType());
        assert oauth2TokenDTO.getExpiresIn() > 0;

        tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.REFRESH_TOKEN.getCode());
        tokenContext.setClientId(TestConstants.CLIENT1_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
        tokenContext.setRefreshToken(oauth2TokenDTO.getRefreshToken());
        OAuth2TokenDTO refreshedDTO = oauth2Service.token(tokenContext);
        assert refreshedDTO != null;
        assert StringUtils.isNotEmpty(refreshedDTO.getAccessToken());
        assert StringUtils.isNotEmpty(refreshedDTO.getRefreshToken());
        assert StringUtils.isNotEmpty(refreshedDTO.getScope());
        assert StringUtils.isNotEmpty(refreshedDTO.getTokenType());
        assert refreshedDTO.getExpiresIn() > 0;
        assert !Objects.equals(refreshedDTO.getAccessToken(), oauth2TokenDTO.getAccessToken());
        assert !Objects.equals(refreshedDTO.getRefreshToken(), oauth2TokenDTO.getRefreshToken());

        authorizeContext = new DefaultAuthorizeContext<>();
        authorizeContext.setResponseType(ResponseType.CODE.getCode());
        authorizeContext.setClientId(TestConstants.CLIENT1_ID);
        authorizeContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        authorizeContext.setScope(TestConstants.CLIENT1_SCOPE);
        authorizeContext.setState(state);
        authorizeContext.setSsoId(ssoId);
        uri = oauth2Service.authorize(authorizeContext);
        uriComponents = UriComponentsBuilder.fromUri(uri).build();
        assert Objects.equals(uriComponents.getQueryParams().getFirst("state"), state);
        code = uriComponents.getQueryParams().getFirst("code");
        String newSsoId = authorizeContext.getSsoId();
        assert StringUtils.isNotEmpty(newSsoId);
        assert !Objects.equals(ssoId, newSsoId);

        tokenContext = new DefaultTokenContext<>();
        tokenContext.setGrantType(GrantType.AUTHORIZATION_CODE.getCode());
        tokenContext.setCode(code);
        tokenContext.setRedirectUri(TestConstants.CLIENT1_REDIRECT_URI);
        tokenContext.setClientId(TestConstants.CLIENT1_ID);
        tokenContext.setClientSecret(TestConstants.CLIENT1_SECRET);
        tokenContext.setScope(TestConstants.CLIENT1_SCOPE);
        OAuth2TokenDTO newOssOAuth2TokenDTO = oauth2Service.token(tokenContext);
        assert newOssOAuth2TokenDTO != null;
        assert !Objects.equals(refreshedDTO, newOssOAuth2TokenDTO);
    }

    @Test
    public void testPasswordMode_SSO_Unsupported() {
        assertThrows(ThrowableErrorCode.class, () -> {
            UserCertificate certificate = new UserCertificate();
            certificate.setUsername(TestConstants.USER2_USERNAME);
            certificate.setPassword(TestConstants.USER2_PASSWORD);
            DefaultTokenContext<UserCertificate> tokenContext = new DefaultTokenContext<>();
            tokenContext.setGrantType(GrantType.PASSWORD.getCode());
            tokenContext.setClientId(TestConstants.CLIENT2_ID);
            tokenContext.setClientSecret(TestConstants.CLIENT2_SECRET);
            tokenContext.setScope(TestConstants.CLIENT2_SCOPE);
            tokenContext.setCertificate(certificate);
            OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(tokenContext);
            assert oauth2TokenDTO != null;
            assert StringUtils.isNotEmpty(oauth2TokenDTO.getAccessToken());
            assert StringUtils.isNotEmpty(oauth2TokenDTO.getRefreshToken());
            assert StringUtils.isNotEmpty(oauth2TokenDTO.getScope());
            assert StringUtils.isNotEmpty(oauth2TokenDTO.getTokenType());
            assert oauth2TokenDTO.getExpiresIn() > 0;
            String ssoId = tokenContext.getSsoId();
            assert ssoId != null;

            tokenContext = new DefaultTokenContext<>();
            tokenContext.setGrantType(GrantType.PASSWORD.getCode());
            tokenContext.setClientId(TestConstants.CLIENT2_ID);
            tokenContext.setClientSecret(TestConstants.CLIENT2_SECRET);
            tokenContext.setScope(TestConstants.CLIENT2_SCOPE);
            tokenContext.setSsoId(ssoId);
            oauth2Service.token(tokenContext);
        });
    }

}
