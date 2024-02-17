package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface AuthorizeContext<CERTIFICATE> {

    /**
     * 必须，表示授权类型
     */
    String getResponseType();

    /**
     * 必须，客户端
     */
    String getClientId();

    /**
     * 必须，表示重定向URI
     */
    String getRedirectUri();

    /**
     * 表示申请的权限范围
     */
    String getScope();

    /**
     * 表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值
     */
    String getState();

    /**
     * 认证信息
     */
    CERTIFICATE getCertificate();

}
