package fun.fengwk.convention4j.oauth2.sdk.controller;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.sdk.config.TokenSessionConfig;
import fun.fengwk.convention4j.oauth2.sdk.context.OAuth2Context;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.service.SdkOAuth2Service;
import fun.fengwk.convention4j.oauth2.sdk.utils.OAuth2UriUtils;
import fun.fengwk.convention4j.oauth2.sdk.utils.TokenCookieUtils;
import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import fun.fengwk.convention4j.springboot.starter.scan.ExcludeComponent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * @author fengwk
 */
@AllArgsConstructor
@ExcludeComponent
@RestController
public class SdkOAuth2Controller<SUBJECT> {

    private final OAuth2Context<SUBJECT> oauth2Context;
    private final SdkOAuth2Service sdkOAuth2TokenService;
    private final OAuth2StateSessionManager oauth2StateSessionManager;
    private final OAuth2SdkProperties oauth2SdkProperties;

    /**
     * 获取授权地址
     *
     * @param redirectUri 重定向地址
     * @param responseType 响应类型
     * @return oauth2授权地址
     */
    @GetMapping("${convention.oauth2.sdk.oauth2-uri-rest-path:oauth2-uri}")
    public Result<String> authenticationCode(
        @RequestParam("responseType") String responseType,
        @RequestParam(value = "redirectUri", required = false) String redirectUri,
        @RequestParam(value = "scope", required = false) String scope) {
        if (redirectUri == null) {
            redirectUri = oauth2SdkProperties.getRedirectUri();
        }
        if (scope == null) {
            scope = oauth2SdkProperties.getScope();
        }
        URI oauth2Uri = OAuth2UriUtils.generateOAuth2Uri(
            oauth2SdkProperties.getOauth2Uri(),
            ResponseType.of(responseType),
            oauth2SdkProperties.getClientId(),
            redirectUri,
            scope,
            oauth2StateSessionManager.generateState());
        return Results.ok(NullSafe.map(oauth2Uri, URI::toASCIIString));
    }

    /**
     * 通过授权码获取OAuth2令牌
     *
     * @param code        授权码
     * @param redirectUri 重定向地址
     * @param state       登陆状态
     * @return OAuth2令牌
     * @throws fun.fengwk.convention4j.api.code.ThrowableErrorCode 如果获取失败将抛出该异常并携带具体错误码
     */
    @PostMapping("${convention.oauth2.sdk.authentication-code-rest-path:authentication-code}")
    public Result<Void> authenticationCode(@RequestParam("code") String code,
                                           @RequestParam("redirectUri") String redirectUri,
                                           @RequestParam(value = "state", required = false) String state,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        OAuth2TokenDTO oauth2Token = sdkOAuth2TokenService.authenticationCode(code, redirectUri, state);
        TokenSessionConfig tokenSessionConfig = oauth2SdkProperties.getTokenSession();
        TokenCookieUtils.setAccessToken(request, response,
            oauth2Token.getAccessToken(), tokenSessionConfig.getMaxStoreSeconds());
        return Results.ok();
    }

    /**
     * 通过授权码获取OAuth2令牌
     *
     * @param accessToken 访问令牌
     * @return OAuth2令牌
     * @throws fun.fengwk.convention4j.api.code.ThrowableErrorCode 如果获取失败将抛出该异常并携带具体错误码
     */
    @DeleteMapping("${convention.oauth2.sdk.revoke-token-rest-path:revoke-token}")
    public Result<Void> revokeToken(@RequestParam(value = "accessToken", required = false) String accessToken,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        if (StringUtils.isBlank(accessToken)) {
            accessToken = TokenCookieUtils.getAccessToken(request);
        }
        if (StringUtils.isNotBlank(accessToken)) {
            sdkOAuth2TokenService.revokeToken(accessToken);
            TokenCookieUtils.deleteAccessToken(request, response);
        }
        return Results.noContent();
    }

    /**
     * 获取主体
     *
     * @param scope 作用域
     * @return 主体
     */
    @GetMapping("${convention.oauth2.sdk.subject-rest-path:subject}")
    public Result<SUBJECT> subject(@RequestParam(value = "scope", required = false) String scope) {
        return Results.ok(oauth2Context.getSubjectRequired(scope));
    }

}
