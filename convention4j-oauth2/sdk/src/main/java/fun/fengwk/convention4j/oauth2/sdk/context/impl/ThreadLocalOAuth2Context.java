package fun.fengwk.convention4j.oauth2.sdk.context.impl;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.oauth2.sdk.client.RefreshTokenClient;
import fun.fengwk.convention4j.oauth2.sdk.client.SubjectClient;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2ContextConfig;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.sdk.config.TokenSessionConfig;
import fun.fengwk.convention4j.oauth2.sdk.context.OAuth2Context;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2StateSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.manager.OAuth2TokenSessionManager;
import fun.fengwk.convention4j.oauth2.sdk.utils.OAuth2UriUtils;
import fun.fengwk.convention4j.oauth2.sdk.utils.TokenCookieUtils;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Slf4j
public class ThreadLocalOAuth2Context<SUBJECT> implements OAuth2Context<SUBJECT> {

    private final ThreadLocal<Context> threadLocalContext = new ThreadLocal<>();
    private final SubjectClient<SUBJECT> oauth2SubjectClient;
    private final RefreshTokenClient refreshTokenClient;
    private final OAuth2TokenSessionManager oauth2TokenSessionManager;
    private final OAuth2StateSessionManager oauth2StateSessionManager;
    private final OAuth2SdkProperties oauth2SdkProperties;

    @Override
    public SUBJECT getSubject() {
        return getSubject(null);
    }

    @Override
    public SUBJECT getSubjectRequired() {
        return getSubjectRequired(null);
    }

    @Override
    public SUBJECT getSubject(String scope) {
        Context context = threadLocalContext.get();
        if (context == null) {
            log.error("No context found in current thread, thread: {}", Thread.currentThread().getName());
            return null;
        }
        // 如果已加载过则无需重复加载
        if (context.isSubjectLoaded()) {
            return context.getSubject();
        }

        HttpServletRequest request = context.getRequest();

        // 获取访问令牌，如果无访问令牌则直接返回null
        String accessToken = TokenCookieUtils.getAccessToken(request);
        if (StringUtils.isBlank(accessToken)) {
            // 设置已加载标识避免重复加载
            context.setSubjectLoaded(true);
            return null;
        }

        // 获取主体
        OAuth2ContextConfig oauth2ContextConfig = oauth2SdkProperties.getOauth2Context();
        TokenSessionConfig tokenSessionConfig = oauth2SdkProperties.getTokenSession();
        Result<SUBJECT> result = oauth2SubjectClient.subject(accessToken, oauth2ContextConfig.getDefaultSubjectScope());
        if (!result.isSuccess() && oauth2ContextConfig.isRefreshable()) {
            // 如果不成功尝试刷新一次令牌
            OAuth2TokenDTO tokenDTO = oauth2TokenSessionManager.get(accessToken);
            // 如果无法通过访问令牌获取到实际令牌则停止刷新并返回null
            if (tokenDTO == null) {
                // 设置已加载标识避免重复加载
                context.setSubjectLoaded(true);
                return null;
            }

            // 尝试刷新令牌
            Result<OAuth2TokenDTO> refreshTokenResult = refreshTokenClient.refreshToken(tokenDTO.getRefreshToken());
            if (refreshTokenResult.isSuccess()) {
                // 重置令牌为刷新后的令牌
                oauth2TokenSessionManager.remove(accessToken);
                tokenDTO = refreshTokenResult.getData();
                oauth2TokenSessionManager.add(tokenDTO);
                // 刷新成功需要写入新的访问令牌
                HttpServletResponse response = context.getResponse();

                TokenCookieUtils.setAccessToken(request, response,
                    tokenDTO.getAccessToken(), tokenSessionConfig.getMaxStoreSeconds());
            } else {
                // 设置已加载标识避免重复加载
                context.setSubjectLoaded(true);
                return null;
            }

            if (scope == null) {
                scope = oauth2ContextConfig.getDefaultSubjectScope();
            }
            result = oauth2SubjectClient.subject(tokenDTO.getAccessToken(), scope);
            if (!result.isSuccess()) {
                // 如果使用刚刷新的令牌仍然无法成功获取到subject可能是服务出现了问题，这里直接降级返回
                log.error("failed to get subject by refreshed token, message: {}, accessToken: {}",
                    refreshTokenResult.getMessage(), tokenDTO.getAccessToken());
                // 设置已加载标识避免重复加载
                context.setSubjectLoaded(true);
                return null;
            }
        }
        context.setSubject(result.getData());
        context.setSubjectLoaded(true);
        return context.getSubject();
    }

    @Override
    public SUBJECT getSubjectRequired(String scope) {
        SUBJECT subject = getSubject(scope);
        if (subject == null) {
            Context context = threadLocalContext.get();
            if (context != null) {
                TokenCookieUtils.deleteAccessToken(context.getRequest(), context.getResponse());
            }
            throw OAuth2UriUtils.injectOAuth2UriIfNecessary(
                OAuth2ErrorCodes.INVALID_ACCESS_TOKEN, oauth2StateSessionManager, oauth2SdkProperties);
        }
        return subject;
    }

    void set(HttpServletRequest request, HttpServletResponse response) {
        Context context = new Context(request, response);
        threadLocalContext.set(context);
    }

    void clear() {
        threadLocalContext.remove();
    }

    @Data
    class Context {
        final HttpServletRequest request;
        final HttpServletResponse response;
        SUBJECT subject;
        boolean subjectLoaded;
    }

}
