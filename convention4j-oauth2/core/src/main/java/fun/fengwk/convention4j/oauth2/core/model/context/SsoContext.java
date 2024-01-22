package fun.fengwk.convention4j.oauth2.core.model.context;

/**
 * @author fengwk
 */
public interface SsoContext {

    /**
     * 获取客户端id
     *
     * @return 客户端id
     */
    String getClientId();

    /**
     * 获取单点登陆id
     *
     * @return 单点登陆id
     */
    String getSsoId();

    /**
     * 设置单点登陆id
     *
     * @param ssoId 单点登陆id
     */
    void setSsoId(String ssoId);

    /**
     * 检查当前上下文是否进行了sso认证
     *
     * @return 是否进行了sso认证
     */
    boolean isSsoAuthenticate();

    /**
     * 设置当前上下文是否使用sso认证
     *
     * @param ssoAuthenticate 是否使用sso认证
     */
    void setSsoAuthenticate(boolean ssoAuthenticate);

}
