package fun.fengwk.convention4j.oauth2.sdk.context;

import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;

/**
 * @author fengwk
 */
public interface OAuth2Context<SUBJECT> {

    /**
     * 获取当前上下文中OAuth2的访问主体
     *
     * @return 如果当前上下文中存在OAuth2访问主体则返回具体的访问主体，否则将返回null
     */
    SUBJECT getSubject();

    /**
     * 获取当前上下文中OAuth2的访问主体，如果无法通过合法令牌获取到将抛出{@link OAuth2ErrorCodes#INVALID_ACCESS_TOKEN}错误码异常，
     * 错误码异常的error字段中将包含需要重定向进行认证的oath2地址
     *
     * @return 如果当前上下文中存在OAuth2访问主体则返回具体的访问主体
     * @throws fun.fengwk.convention4j.api.code.ThrowableErrorCode 如果没有合法令牌将抛出该异常
     */
    SUBJECT getSubjectRequired();

    /**
     * 支持指定作用域
     * @see #getSubject()
     */
    SUBJECT getSubject(String scope);

    /**
     * 支持指定作用域
     * @see #getSubjectRequired()
     */
    SUBJECT getSubjectRequired(String scope);

}
