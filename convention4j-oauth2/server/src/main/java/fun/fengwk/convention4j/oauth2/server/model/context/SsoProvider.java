package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface SsoProvider {

    /**
     * 获取单点登陆id
     *
     * @return 单点登陆id
     */
    String getSsoId();

    /**
     * 检查当前上下文是否进行了sso认证
     *
     * @return 是否进行了sso认证
     */
    boolean isSsoAuthenticate();

}
