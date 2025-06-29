package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface SsoContext extends SsoProvider {

    String EMPTY_SSO_DOMAIN = "";

    /**
     * 获取客户端id
     *
     * @return 客户端id
     */
    String getClientId();

    /**
     * 设置单点登陆id
     *
     * @param ssoId 单点登陆id
     */
    void setSsoId(String ssoId);

    /**
     * 设置当前上下文是否使用sso认证
     *
     * @param ssoAuthenticate 是否使用sso认证
     */
    void setSsoAuthenticate(boolean ssoAuthenticate);

}
