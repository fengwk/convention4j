package fun.fengwk.convention4j.oauth2.core.controller;

import fun.fengwk.convention4j.api.code.ThrowableErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.core.OAuth2Properties;
import fun.fengwk.convention4j.oauth2.core.model.context.DefaultAuthorizeContext;
import fun.fengwk.convention4j.oauth2.core.model.context.DefaultTokenContext;
import fun.fengwk.convention4j.oauth2.core.service.OAuth2Service;
import fun.fengwk.convention4j.oauth2.core.util.SsoCookieUtils;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import fun.fengwk.convention4j.springboot.starter.scan.ExcludeComponent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

/**
 * @author fengwk
 */
@AllArgsConstructor
@ExcludeComponent
@RequestMapping("${convention.oauth2.oauth2-rest-prefix:}")
@RestController
public class OAuth2Controller<SUBJECT, CERTIFICATE> {

    private final OAuth2Service<SUBJECT, CERTIFICATE> oauth2Service;
    private final OAuth2Properties oauth2Properties;
    private final Type certificateType;

    /**
     * 单点登陆authorize
     *
     * @return 授权uri
     */
    @PostMapping(value = "/sso")
    public Result<String> sso(
        @RequestParam("responseType") String responseType,
        @RequestParam("clientId") String clientId,
        @RequestParam("redirectUri") String redirectUri,
        @RequestParam(value = "scope", required = false) String scope,
        @RequestParam(value = "state", required = false) String state,
        @RequestBody(required = false) String certificateJson,
        HttpServletRequest request) {
        CERTIFICATE certificate = null;
        if (StringUtils.isNotBlank(certificateJson)) {
            certificate = GsonUtils.fromJson(certificateJson, certificateType);
        }
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
            return Results.ok(uri.toASCIIString());
        } catch (Exception ex) {
            // 如果sso认证失败则返回空结果
            return Results.ok();
        }
    }

    /**
     * 授权码模式、隐式模式
     *
     * @return 授权uri
     */
    @PostMapping(value = "/authorize")
    public Result<String> authorize(
        @RequestParam("responseType") String responseType,
        @RequestParam("clientId") String clientId,
        @RequestParam("redirectUri") String redirectUri,
        @RequestParam(value = "scope", required = false) String scope,
        @RequestParam(value = "state", required = false) String state,
        @RequestBody(required = false) String certificateJson,
        HttpServletRequest request,
        HttpServletResponse response) {
        CERTIFICATE certificate = null;
        if (StringUtils.isNotBlank(certificateJson)) {
            certificate = GsonUtils.fromJson(certificateJson, certificateType);
        }
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
            SsoCookieUtils.setSsoId(request, response, context, ssoIdMap, oauth2Properties.getSsoMaxStoreSeconds());
            return Results.ok(uri.toASCIIString());
        } catch (ThrowableErrorCode errorCode) {
            SsoCookieUtils.deleteSsoId(request, response, context, ssoIdMap, oauth2Properties.getSsoMaxStoreSeconds());
            return Results.error(errorCode);
        }
    }

    /**
     * 授权码模式、客户端模式、密码模式、刷新令牌（clientId、clientSecret、refreshToken）
     *
     * @return 令牌
     */
    @PostMapping("/token")
    public Result<OAuth2TokenDTO> token(
        @RequestParam("grantType") String grantType,
        @RequestParam(value = "code", required = false) String code,
        @RequestParam("redirectUri") String redirectUri,
        @RequestParam("clientId") String clientId,
        @RequestParam("clientSecret") String clientSecret,
        @RequestParam(value = "scope", required = false) String scope,
        @RequestParam(value = "subjectId", required = false) String subjectId,
        @RequestParam(value = "refreshToken", required = false) String refreshToken,
        @RequestBody(required = false) String certificateJson,
        HttpServletRequest request,
        HttpServletResponse response) {
        CERTIFICATE certificate = null;
        if (StringUtils.isNotBlank(certificateJson)) {
            certificate = GsonUtils.fromJson(certificateJson, certificateType);
        }
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
            SsoCookieUtils.setSsoId(request, response, context, ssoIdMap, oauth2Properties.getSsoMaxStoreSeconds());
            return Results.created(oauth2TokenDTO);
        } catch (ThrowableErrorCode errorCode) {
            SsoCookieUtils.deleteSsoId(request, response, context, ssoIdMap, oauth2Properties.getSsoMaxStoreSeconds());
            return Results.error(errorCode);
        }
    }

    /**
     * 获取授权访问主体信息，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param accessToken 访问令牌
     * @param scope       主动指定作用域
     * @param request     header中设置Authorization
     * @return 主体信息
     */
    @GetMapping("/subject")
    public Result<SUBJECT> subject(
        @RequestParam(value = "accessToken", required = false) String accessToken,
        @RequestParam(value = "scope", required = false) String scope,
        HttpServletRequest request) {
        if (StringUtils.isBlank(accessToken)) {
            String authorization = request.getHeader(TokenType.AUTHORIZATION);
            accessToken = TokenType.BEARER.parseAuthorization(authorization);
        }
        if (StringUtils.isBlank(accessToken)) {
            return Results.error(OAuth2ErrorCodes.INVALID_ACCESS_TOKEN);
        }
        SUBJECT subject = oauth2Service.subject(accessToken, scope);
        return Results.ok(subject);
    }

    /**
     * 回收令牌，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param accessToken 访问令牌
     * @param request     header中设置Authorization
     */
    @DeleteMapping("/token")
    public Result<Void> revokeToken(
        @RequestParam(value = "accessToken", required = false) String accessToken,
        HttpServletRequest request) {
        if (StringUtils.isBlank(accessToken)) {
            String authorization = request.getHeader(TokenType.AUTHORIZATION);
            accessToken = TokenType.BEARER.parseAuthorization(authorization);
        }
        if (StringUtils.isNotBlank(accessToken)) {
            oauth2Service.revokeToken(accessToken);
        }
        return Results.noContent();
    }

}
