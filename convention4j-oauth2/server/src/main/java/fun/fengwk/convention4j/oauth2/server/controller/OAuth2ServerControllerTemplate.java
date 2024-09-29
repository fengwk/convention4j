package fun.fengwk.convention4j.oauth2.server.controller;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.server.model.context.DefaultAuthorizeContext;
import fun.fengwk.convention4j.oauth2.server.model.context.DefaultTokenContext;
import fun.fengwk.convention4j.oauth2.server.properties.OAuth2ServerProperties;
import fun.fengwk.convention4j.oauth2.server.service.OAuth2Service;
import fun.fengwk.convention4j.oauth2.server.util.SsoCookieUtils;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public abstract class OAuth2ServerControllerTemplate<SUBJECT, CERTIFICATE> {

    protected final OAuth2ServerProperties oauth2ServerProperties;
    protected final OAuth2Service<SUBJECT, CERTIFICATE> oauth2Service;

    /**
     * 单点登陆authorize
     *
     * @return 授权uri
     */
    public Result<String> sso(
        String responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        HttpServletRequest request) {
        DefaultAuthorizeContext<CERTIFICATE> context = new DefaultAuthorizeContext<>();
        context.setResponseType(responseType);
        context.setClientId(clientId);
        context.setRedirectUri(redirectUri);
        context.setScope(scope);
        context.setState(state);
        Map<String, String> ssoIdMap = SsoCookieUtils.getSsoIdMap(request);
        context.setSsoId(ssoIdMap.get(clientId));
        try {
            URI uri = oauth2Service.authorize(context);
            log.debug("SSO authorize success, context: {}, uri: {}", context, uri);
            return Results.ok(uri.toASCIIString());
        } catch (Exception ex) {
            log.debug("SSO authorize failed, context: {}", context, ex);
            // 如果sso认证失败则返回空结果
            return Results.ok();
        }
    }

    /**
     * 授权码模式、隐式模式
     *
     * @return 授权uri
     */
    public Result<String> authorize(
        String responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        CERTIFICATE certificate,
        HttpServletRequest request,
        HttpServletResponse response) {
        DefaultAuthorizeContext<CERTIFICATE> context = new DefaultAuthorizeContext<>();
        context.setResponseType(responseType);
        context.setClientId(clientId);
        context.setRedirectUri(redirectUri);
        context.setScope(scope);
        context.setState(state);
        context.setCertificate(certificate);
        Map<String, String> ssoIdMap = SsoCookieUtils.getSsoIdMap(request);
        context.setSsoId(ssoIdMap.get(clientId));
        try {
            URI uri = oauth2Service.authorize(context);
            log.debug("Authorize success, context: {}, uri: {}", context, uri);
            SsoCookieUtils.setSsoId(request, response, context, ssoIdMap, oauth2ServerProperties.getSsoStoreSeconds());
            return Results.ok(uri.toASCIIString());
        } catch (Exception ex) {
            log.debug("Authorize failed, context: {}", context);
            SsoCookieUtils.deleteSsoId(request, response, context, ssoIdMap, oauth2ServerProperties.getSsoStoreSeconds());
            throw ex;
        }
    }

    /**
     * 授权码模式、客户端模式、密码模式、刷新令牌（clientId、clientSecret、refreshToken）
     *
     * @return 令牌
     */
    public Result<OAuth2TokenDTO> token(
        String grantType,
        String code,
        String redirectUri,
        String clientId,
        String clientSecret,
        String scope,
        String subjectId,
        String refreshToken,
        CERTIFICATE certificate,
        HttpServletRequest request,
        HttpServletResponse response) {
        DefaultTokenContext<CERTIFICATE> context = new DefaultTokenContext<>();
        context.setGrantType(grantType);
        context.setCode(code);
        context.setRedirectUri(redirectUri);
        context.setClientId(clientId);
        context.setClientSecret(clientSecret);
        context.setScope(scope);
        context.setSubjectId(subjectId);
        context.setRefreshToken(refreshToken);
        context.setCertificate(certificate);
        Map<String, String> ssoIdMap = SsoCookieUtils.getSsoIdMap(request);
        context.setSsoId(ssoIdMap.get(clientId));
        try {
            OAuth2TokenDTO oauth2TokenDTO = oauth2Service.token(context);
            log.debug("Token success, context: {}, oauth2TokenDTO: {}", context, oauth2TokenDTO);
            SsoCookieUtils.setSsoId(request, response, context, ssoIdMap, oauth2ServerProperties.getSsoStoreSeconds());
            return Results.created(oauth2TokenDTO);
        } catch (Exception ex) {
            log.debug("Token failed, context: {}", context);
            SsoCookieUtils.deleteSsoId(request, response, context, ssoIdMap, oauth2ServerProperties.getSsoStoreSeconds());
            throw ex;
        }
    }

    /**
     * 获取授权访问主体信息，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param authorization 授权信息，{@link TokenType#buildAuthorization(String)}
     * @param scope         主动指定作用域
     * @return 主体信息
     */
    public Result<SUBJECT> subject(
        String authorization,
        String scope) {
        String accessToken = TokenType.BEARER.parseAccessToken(authorization);
        SUBJECT subject = oauth2Service.subject(accessToken, scope);
        log.debug("Subject success, accessToken: {}, scope: {}, subject: {}", accessToken, scope, subject);
        return Results.ok(subject);
    }

    /**
     * 回收令牌，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param authorization 授权信息，{@link TokenType#buildAuthorization(String)}
     */
    public Result<Void> revokeToken(String authorization) {
        String accessToken = TokenType.BEARER.parseAccessToken(authorization);
        oauth2Service.revokeToken(accessToken);
        log.debug("Revoke token success, accessToken: {}", accessToken);
        return Results.ok();
    }

}
